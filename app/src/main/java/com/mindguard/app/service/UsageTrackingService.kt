package com.mindguard.app.service

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.mindguard.app.data.repository.AppUsageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UsageTrackingService(
    private val context: Context, private val appUsageRepository: AppUsageRepository
) {

    private val usageStatsManager: UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun updateAppUsageData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis

                // Get usage for today (from midnight)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startTime = calendar.timeInMillis

                // Get usage stats for today
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, startTime, endTime
                )

                // Update each monitored app's usage
                updateMonitoredAppsUsage(usageStats)

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun updateMonitoredAppsUsage(usageStats: List<UsageStats>) {
        try {
            // Get all monitored apps (using first() to get current snapshot, not collect)
            val monitoredApps = appUsageRepository.getAllEnabledApps().first()

            for (app in monitoredApps) {
                // Find usage stats for this app
                val appUsageStats = usageStats.find { it.packageName == app.packageName }

                if (appUsageStats != null) {
                    // Convert from milliseconds to minutes
                    val usageInMinutes = (appUsageStats.totalTimeInForeground / (1000 * 60)).toInt()

                    // Check if we need to reset daily usage
                    val shouldReset = shouldResetDailyUsage(app.lastResetDate)

                    val updatedUsage = if (shouldReset) {
                        // Reset to today's usage
                        usageInMinutes.toLong()
                    } else {
                        // Update current usage
                        usageInMinutes.toLong()
                    }

                    // Update the app with new usage data
                    val updatedApp = app.copy(
                        currentUsage = updatedUsage,
                        lastResetDate = if (shouldReset) System.currentTimeMillis() else app.lastResetDate
                    )

                    appUsageRepository.updateApp(updatedApp)
                }
            }
        } catch (e: Exception) {
            // Handle any errors gracefully
            println("Error updating monitored apps usage: ${e.message}")
        }
    }

    private fun shouldResetDailyUsage(lastResetDate: Long): Boolean {
        val lastReset = Calendar.getInstance().apply {
            timeInMillis = lastResetDate
        }

        val now = Calendar.getInstance()

        // Reset if last reset was on a different day
        return lastReset.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || lastReset.get(
            Calendar.YEAR
        ) != now.get(Calendar.YEAR)
    }

    fun getTodayScreenTime(): Long {
        return try {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis

            // Get usage for today
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            // Calculate total screen time in minutes
            usageStats.sumOf { it.totalTimeInForeground } / (1000 * 60)

        } catch (e: Exception) {
            0L
        }
    }

    fun getAppUsageForToday(packageName: String): Long {
        return try {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            val appStats = usageStats.find { it.packageName == packageName }
            (appStats?.totalTimeInForeground ?: 0L) / (1000 * 60) // Convert to minutes

        } catch (e: Exception) {
            0L
        }
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    fun getInstalledSocialMediaApps(): List<Pair<String, String>> {
        val socialMediaPackages = listOf(
            "com.instagram.android" to "Instagram",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.facebook.katana" to "Facebook",
            "com.twitter.android" to "Twitter",
            "com.snapchat.android" to "Snapchat",
            "com.linkedin.android" to "LinkedIn",
            "com.pinterest" to "Pinterest",
            "com.reddit.frontpage" to "Reddit",
            "com.whatsapp" to "WhatsApp",
            "com.discord" to "Discord",
            "com.telegram.messenger" to "Telegram",
            "com.youtube.android" to "YouTube"
        )

        val packageManager = context.packageManager
        val installedApps = mutableListOf<Pair<String, String>>()

        for ((packageName, appName) in socialMediaPackages) {
            try {
                packageManager.getPackageInfo(packageName, 0)
                installedApps.add(packageName to appName)
            } catch (e: PackageManager.NameNotFoundException) {
                // App not installed
            }
        }

        return installedApps
    }

    fun formatTime(minutes: Long): String {
        return when {
            minutes < 60 -> "${minutes}min"
            minutes < 1440 -> "${minutes / 60}h ${minutes % 60}min"
            else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
        }
    }

    fun getWeeklyUsageStats(packageName: String): Map<String, Long> {
        val weekStats = mutableMapOf<String, Long>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 6 downTo 0) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - i)

            val endTime = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val startTime = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            try {
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, startTime, endTime
                )

                val dayUsage =
                    usageStats.find { it.packageName == packageName }?.totalTimeInForeground?.div(
                        1000 * 60
                    ) ?: 0L

                weekStats[dateFormat.format(calendar.time)] = dayUsage

            } catch (e: Exception) {
                weekStats[dateFormat.format(calendar.time)] = 0L
            }

            calendar.time = Date()
        }

        return weekStats
    }
}