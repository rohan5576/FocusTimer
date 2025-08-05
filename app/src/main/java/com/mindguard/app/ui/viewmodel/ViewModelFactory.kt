package com.mindguard.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindguard.app.data.repository.AppUsageRepository
import com.mindguard.app.data.repository.DailyStatisticsRepository
import com.mindguard.app.data.repository.MotivationalQuoteRepository

class ViewModelFactory(
    private val appUsageRepository: AppUsageRepository,
    private val motivationalQuoteRepository: MotivationalQuoteRepository,
    private val dailyStatisticsRepository: DailyStatisticsRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(appUsageRepository, dailyStatisticsRepository, context) as T
            }

            modelClass.isAssignableFrom(AppsViewModel::class.java) -> {
                AppsViewModel(appUsageRepository) as T
            }

            modelClass.isAssignableFrom(QuotesViewModel::class.java) -> {
                QuotesViewModel(motivationalQuoteRepository, context) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(context) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 