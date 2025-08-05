package com.mindguard.app.service

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.mindguard.app.MindGuardApplication
import com.mindguard.app.R
import com.mindguard.app.data.model.AppUsage
import com.mindguard.app.data.repository.AppUsageRepository
import com.mindguard.app.data.repository.DailyStatisticsRepository
import com.mindguard.app.util.ArithmeticProblemGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MindGuardAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MindGuardAccessibility"
    }

    private lateinit var appUsageRepository: AppUsageRepository
    private lateinit var dailyStatisticsRepository: DailyStatisticsRepository

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var arithmeticDialogView: View? = null
    private var currentBlockedApp: String? = null
    private var currentAppPackage: String? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()

        // Initialize repositories
        val app = applicationContext as MindGuardApplication
        appUsageRepository = app.appUsageRepository
        dailyStatisticsRepository = app.dailyStatisticsRepository

        // Debug: Log service connection
        Log.d(TAG, "🔗 Accessibility Service Connected!")

        // Check permissions
        com.mindguard.app.debug.AccessibilityTestHelper.logAccessibilityStatus(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    val packageName = it.packageName?.toString()
                    if (packageName != null && packageName != this.packageName) {
                        if (currentAppPackage != packageName) {
                            currentAppPackage = packageName
                            Log.d(TAG, "App switched to: $packageName")
                            checkAppUsage(packageName)
                        }
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    private fun checkAppUsage(packageName: String) {
        // Only skip our own app and core system UI
        if (packageName == this.packageName || packageName == "com.android.systemui" || packageName == "android") {
            return
        }

        Log.d(TAG, "🔍 Checking app usage for: $packageName")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Looking for app in database: $packageName")

                val monitoredApp = appUsageRepository.getAppByPackage(packageName)

                if (monitoredApp != null && monitoredApp.isEnabled) {
                    Log.d(TAG, "✅ Found monitored app: ${monitoredApp.appName}")
                    Log.d(
                        TAG,
                        "📊 Current usage: ${monitoredApp.currentUsage}/${monitoredApp.dailyTimeLimit} minutes"
                    )

                    // For debugging: Force trigger if Instagram (our test app)
                    val shouldTrigger = if (packageName == "com.instagram.android") {
                        Log.d(TAG, "🧪 DEBUGGING: Force trigger for Instagram test")
                        true
                    } else {
                        // Update current usage from UsageStatsManager first
                        val usageTrackingService = UsageTrackingServiceNew(
                            this@MindGuardAccessibilityService, appUsageRepository
                        )
                        usageTrackingService.updateMonitoredAppsUsage()

                        // Re-fetch the updated app data
                        val updatedApp = appUsageRepository.getAppByPackage(packageName)
                        updatedApp != null && updatedApp.currentUsage >= updatedApp.dailyTimeLimit
                    }

                    if (shouldTrigger) {
                        Log.d(
                            TAG,
                            "🚨 TIME LIMIT EXCEEDED! Triggering overlay for ${monitoredApp.appName}"
                        )
                        mainHandler.post {
                            showBlockingOverlay(packageName, monitoredApp)
                        }
                    } else {
                        Log.d(TAG, "✅ App within limits: ${monitoredApp.appName}")
                        // Hide overlay if this app was previously blocked
                        if (currentBlockedApp == packageName) {
                            hideOverlay()
                        }
                    }
                } else {
                    Log.d(TAG, "ℹ️ App not monitored: $packageName")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error checking app usage for $packageName", e)
            }
        }
    }

    private fun showBlockingOverlay(packageName: String, appUsage: AppUsage) {
        Log.d(TAG, "🔥 showBlockingOverlay called for $packageName")
        Log.d(TAG, "App usage: ${appUsage.currentUsage}/${appUsage.dailyTimeLimit} minutes")

        // Check permissions first
        if (!com.mindguard.app.debug.AccessibilityTestHelper.hasOverlayPermission(this)) {
            Log.e(TAG, "❌ OVERLAY PERMISSION NOT GRANTED!")
            Toast.makeText(
                this, "Overlay permission required! Please enable in Settings.", Toast.LENGTH_LONG
            ).show()
            return
        }

        if (currentBlockedApp == packageName) {
            Log.d(TAG, "Already blocking this app, skipping")
            return
        }

        currentBlockedApp = packageName

        mainHandler.post {
            try {
                Log.d(TAG, "🎯 Creating overlay UI on main thread")

                // Hide existing overlay first
                hideOverlay()

                if (windowManager == null) {
                    windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                    Log.d(TAG, "WindowManager initialized")
                }

                // Create fresh overlay view
                try {
                    overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_blocking, null)
                    Log.d(TAG, "✅ Overlay view inflated successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to inflate overlay layout", e)
                    showFallbackToast(appUsage)
                    return@post
                }

                // Use more compatible window parameters
                val params = WindowManager.LayoutParams().apply {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.MATCH_PARENT
                    type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
                    }
                    flags =
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    format = PixelFormat.TRANSLUCENT
                    gravity = Gravity.CENTER
                }

                Log.d(TAG, "🚀 Window params created, attempting to add overlay")

                try {
                    windowManager?.addView(overlayView, params)
                    Log.d(TAG, "✅ OVERLAY SUCCESSFULLY ADDED TO WINDOW!")

                    // Setup buttons
                    setupOverlayButtons(appUsage)

                    // Show immediately visible toast for confirmation
                    Toast.makeText(
                        this@MindGuardAccessibilityService,
                        "Time limit reached! Overlay active.",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Auto-show math problem for immediate testing
                    mainHandler.postDelayed({
                        Log.d(TAG, "🧮 Auto-showing math problem for testing")
                        showArithmeticProblem(appUsage)
                    }, 1500)

                } catch (e: Exception) {
                    Log.e(TAG, "❌ CRITICAL: Failed to add overlay to window", e)
                    Log.e(TAG, "Error details: ${e.message}")
                    Log.e(TAG, "Error cause: ${e.cause}")
                    Log.e(TAG, "Error stack: ${e.stackTrace.contentToString()}")

                    showFallbackToast(appUsage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ CRITICAL: Error in main overlay creation", e)
                showFallbackToast(appUsage)
            }
        }
    }

    private fun showFallbackToast(appUsage: AppUsage) {
        mainHandler.post {
            Toast.makeText(
                this@MindGuardAccessibilityService,
                "⏰ TIME LIMIT REACHED for ${appUsage.appName}! Please take a break.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupOverlayButtons(appUsage: AppUsage) {
        overlayView?.findViewById<View>(R.id.btnSolveProblem)?.setOnClickListener {
            showArithmeticProblem(appUsage)
        }

        overlayView?.findViewById<View>(R.id.btnTakeBreak)?.setOnClickListener {
            // Increment breaks taken in statistics
            CoroutineScope(Dispatchers.IO).launch {
                dailyStatisticsRepository.incrementBreaksTaken()
            }

            hideOverlay()
            goBack()
        }
    }

    private fun showArithmeticProblem(appUsage: AppUsage) {
        val sharedPrefs =
            getSharedPreferences("mindguard_settings", android.content.Context.MODE_PRIVATE)
        val difficultyString = sharedPrefs.getString("difficulty_level", "MEDIUM")

        val difficulty = when (difficultyString) {
            "EASY" -> com.mindguard.app.data.model.ProblemDifficulty.EASY
            "HARD" -> com.mindguard.app.data.model.ProblemDifficulty.HARD
            else -> com.mindguard.app.data.model.ProblemDifficulty.MEDIUM
        }

        val problem = ArithmeticProblemGenerator.generateProblem(difficulty)

        // Show arithmetic problem dialog
        showArithmeticDialog(problem, appUsage)
    }

    private fun showArithmeticDialog(
        problem: com.mindguard.app.data.model.ArithmeticProblem, appUsage: AppUsage
    ) {
        mainHandler.post {
            try {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_arithmetic, null)

                // Set up the problem
                val questionText = dialogView.findViewById<TextView>(R.id.tvQuestion)
                val answerInput = dialogView.findViewById<EditText>(R.id.etAnswer)
                val submitButton = dialogView.findViewById<View>(R.id.btnSubmit)
                val skipButton = dialogView.findViewById<View>(R.id.btnSkip)

                questionText?.text = problem.question

                submitButton?.setOnClickListener {
                    val userAnswer = answerInput?.text?.toString()?.toIntOrNull()
                    if (userAnswer == problem.answer) {
                        // Correct answer
                        Toast.makeText(
                            this@MindGuardAccessibilityService,
                            "Correct! Extra time granted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        grantExtraTime(appUsage, problem.extraTimeReward)

                        // Increment problems solved in statistics
                        CoroutineScope(Dispatchers.IO).launch {
                            dailyStatisticsRepository.incrementProblemsSolved()
                        }

                        hideArithmeticDialog()
                        hideOverlay()
                    } else {
                        // Wrong answer
                        Toast.makeText(
                            this@MindGuardAccessibilityService,
                            "Incorrect. Try again or take a break.",
                            Toast.LENGTH_SHORT
                        ).show()
                        answerInput?.text?.clear()
                    }
                }

                skipButton?.setOnClickListener {
                    // Increment breaks taken in statistics
                    CoroutineScope(Dispatchers.IO).launch {
                        dailyStatisticsRepository.incrementBreaksTaken()
                    }

                    hideArithmeticDialog()
                    goBack()
                }

                // Create window parameters for the arithmetic dialog
                val dialogParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        WindowManager.LayoutParams.TYPE_PHONE
                    },
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT
                )

                dialogParams.gravity = Gravity.CENTER

                arithmeticDialogView = dialogView
                windowManager?.addView(dialogView, dialogParams)

            } catch (e: Exception) {
                // Fallback: just grant extra time if dialog fails
                grantExtraTime(appUsage, problem.extraTimeReward)
                hideOverlay()
            }
        }
    }

    private fun grantExtraTime(appUsage: AppUsage, extraMinutes: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedApp = appUsage.copy(
                    dailyTimeLimit = appUsage.dailyTimeLimit + extraMinutes
                )
                appUsageRepository.updateApp(updatedApp)
                Log.d(TAG, "Granted $extraMinutes extra minutes to ${appUsage.appName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error granting extra time", e)
            }
        }
    }

    private fun hideOverlay() {
        mainHandler.post {
            try {
                if (overlayView != null) {
                    windowManager?.removeView(overlayView)
                    overlayView = null
                }
                currentBlockedApp = null
            } catch (e: Exception) {
                Log.e(TAG, "Error hiding overlay", e)
            }
        }
    }

    private fun hideArithmeticDialog() {
        mainHandler.post {
            try {
                if (arithmeticDialogView != null) {
                    windowManager?.removeView(arithmeticDialogView)
                    arithmeticDialogView = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error hiding arithmetic dialog", e)
            }
        }
    }

    private fun goBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
        hideArithmeticDialog()
    }
}