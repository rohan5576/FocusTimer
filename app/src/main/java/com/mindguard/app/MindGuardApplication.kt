package com.mindguard.app

import android.app.Application
import androidx.work.WorkManager
import com.mindguard.app.data.database.MindGuardDatabase
import com.mindguard.app.data.repository.AppUsageRepository
import com.mindguard.app.data.repository.MotivationalQuoteRepository
import com.mindguard.app.data.repository.DailyStatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MindGuardApplication : Application() {
    
    lateinit var database: MindGuardDatabase
    lateinit var appUsageRepository: AppUsageRepository
    lateinit var motivationalQuoteRepository: MotivationalQuoteRepository
    lateinit var dailyStatisticsRepository: DailyStatisticsRepository
    
    override fun onCreate() {
        super.onCreate()
        
        database = MindGuardDatabase.getDatabase(this)
        appUsageRepository = AppUsageRepository(database.appUsageDao())
        motivationalQuoteRepository = MotivationalQuoteRepository(database.motivationalQuoteDao())
        dailyStatisticsRepository = DailyStatisticsRepository(database.dailyStatisticsDao())
        
        initializeSampleData()
        
        WorkManager.initialize(this, androidx.work.Configuration.Builder().build())
    }
    
    private fun initializeSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Don't initialize dummy apps - let users add their own from device
                // appUsageRepository.initializeDefaultApps()
                motivationalQuoteRepository.initializeDefaultQuotes()
            } catch (e: Exception) {
            }
        }
    }
} 