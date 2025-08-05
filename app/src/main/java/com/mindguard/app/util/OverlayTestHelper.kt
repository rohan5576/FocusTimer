package com.mindguard.app.util

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

object OverlayTestHelper {
    
    private const val TAG = "OverlayTestHelper"
    private var testOverlay: View? = null
    private var windowManager: WindowManager? = null
    
    fun testOverlayCapability(context: Context): Boolean {
        return try {
            if (!hasOverlayPermission(context)) {
                Log.e(TAG, "No overlay permission")
                return false
            }
            
            showTestOverlay(context)
            
            // Remove test overlay after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                hideTestOverlay()
            }, 2000)
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Overlay test failed", e)
            false
        }
    }
    
    private fun showTestOverlay(context: Context) {
        try {
            if (windowManager == null) {
                windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            
            // Create simple test view
            val testView = TextView(context).apply {
                text = "🎯 OVERLAY TEST SUCCESS!\nThis confirms overlay capability"
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#FF4444"))
                setPadding(40, 40, 40, 40)
                gravity = Gravity.CENTER
            }
            
            val params = WindowManager.LayoutParams().apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.CENTER
            }
            
            windowManager?.addView(testView, params)
            testOverlay = testView
            
            Log.d(TAG, "✅ Test overlay added successfully!")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show test overlay", e)
            throw e
        }
    }
    
    private fun hideTestOverlay() {
        try {
            testOverlay?.let { view ->
                windowManager?.removeView(view)
                testOverlay = null
                Log.d(TAG, "Test overlay removed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing test overlay", e)
        }
    }
    
    private fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
}