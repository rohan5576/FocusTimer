package com.mindguard.app.service

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.mindguard.app.data.repository.AppUsageRepository
import kotlinx.coroutines.flow.first
import java.util.*

class UsageTrackingServiceNew(
    private val context: Context,
    private val appUsageRepository: AppUsageRepository
) {
    
    fun hasUsageStatsPermission(): Boolean {
        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (60 * 1000) // Last minute
            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )
            usageStats.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    suspend fun updateMonitoredAppsUsage() {
        try {
            if (!hasUsageStatsPermission()) {
                println("Usage stats permission not granted")
                return
            }

            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            
            // Get today's usage (from midnight)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            println("Found ${usageStats.size} usage stats from ${Date(startTime)} to ${Date(endTime)}")

            // Get monitored apps - use first() to prevent infinite loop
            val monitoredApps = appUsageRepository.getAllEnabledApps().first()
            println("Updating usage for ${monitoredApps.size} monitored apps")

            monitoredApps.forEach { app ->
                val usageStat = usageStats.find { it.packageName == app.packageName }
                val totalTimeInForeground = usageStat?.totalTimeInForeground ?: 0L
                val usageInMinutes = (totalTimeInForeground / (1000 * 60)).toInt()

                println("App: ${app.appName} (${app.packageName}) - Usage: ${usageInMinutes} minutes")

                // Update the app usage in database
                val updatedApp = app.copy(currentUsage = usageInMinutes.toLong())
                appUsageRepository.updateApp(updatedApp)
            }
        } catch (e: Exception) {
            println("Error updating app usage: ${e.message}")
            e.printStackTrace()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    suspend fun getTotalScreenTimeToday(): Long {
        return try {
            if (!hasUsageStatsPermission()) {
                return 0L
            }

            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            
            // Get today's usage (from midnight)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            // Sum all app usage for total screen time
            val totalUsageMs = usageStats.sumOf { it.totalTimeInForeground }
            val totalUsageMinutes = (totalUsageMs / (1000 * 60))
            
            println("Total screen time today: $totalUsageMinutes minutes")
            totalUsageMinutes
        } catch (e: Exception) {
            println("Error getting total screen time: ${e.message}")
            0L
        }
    }
}