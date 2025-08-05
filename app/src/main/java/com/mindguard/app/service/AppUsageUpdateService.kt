package com.mindguard.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mindguard.app.MindGuardApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppUsageUpdateService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var updateJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPeriodicUpdates()
        return START_STICKY
    }

    private fun startPeriodicUpdates() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isActive) {
                try {
                    val app = application as MindGuardApplication
                    val usageTrackingService =
                        UsageTrackingServiceNew(this@AppUsageUpdateService, app.appUsageRepository)
                    usageTrackingService.updateMonitoredAppsUsage()
                    println("Periodic usage update completed")
                } catch (e: Exception) {
                    println("Error in periodic usage update: ${e.message}")
                }
                delay(30000) // Update every 30 seconds
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        serviceScope.cancel()
    }
}