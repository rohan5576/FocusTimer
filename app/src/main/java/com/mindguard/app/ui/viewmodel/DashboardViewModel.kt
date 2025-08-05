package com.mindguard.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.app.data.model.AppUsage
import com.mindguard.app.data.repository.AppUsageRepository
import com.mindguard.app.data.repository.DailyStatisticsRepository
import com.mindguard.app.service.UsageTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val monitoredApps: List<AppUsage> = emptyList(),
    val totalScreenTime: Long = 0,
    val problemsSolved: Int = 0,
    val breaksTaken: Int = 0,
    val timeSaved: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val appUsageRepository: AppUsageRepository,
    private val dailyStatisticsRepository: DailyStatisticsRepository,
    private val context: Context
) : ViewModel() {

    private val usageTrackingService = UsageTrackingService(context, appUsageRepository)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Update app usage data from system
                usageTrackingService.updateAppUsageData()

                appUsageRepository.getAllEnabledApps().collect { apps ->
                    // Get real screen time from usage stats
                    val realScreenTime = usageTrackingService.getTodayScreenTime()

                    // Update each app with real usage data
                    val updatedApps = apps.map { app ->
                        val realUsage = usageTrackingService.getAppUsageForToday(app.packageName)
                        app.copy(currentUsage = realUsage)
                    }

                    // Get real statistics from database
                    val todayStats = dailyStatisticsRepository.getTodayStatistics()

                    // Calculate and update time saved
                    val timeSaved = calculateTimeSaved(updatedApps)
                    dailyStatisticsRepository.updateTimeSaved(timeSaved)
                    dailyStatisticsRepository.updateTodayScreenTime(realScreenTime)

                    _uiState.value = _uiState.value.copy(
                        monitoredApps = updatedApps,
                        totalScreenTime = realScreenTime,
                        timeSaved = timeSaved,
                        problemsSolved = todayStats.problemsSolved,
                        breaksTaken = todayStats.breaksTaken,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message, isLoading = false
                )
            }
        }
    }

    private fun calculateTimeSaved(apps: List<AppUsage>): Long {
        return apps.sumOf { app ->
            // Calculate how much time would have been wasted beyond the limit
            val overLimit = (app.currentUsage - app.dailyTimeLimit).coerceAtLeast(0)
            // Estimate time saved through blocks and interventions (roughly 50% of over-limit time)
            (overLimit * 0.5).toLong()
        }
    }


    fun refreshData() {
        // Reset daily statistics and reload
        viewModelScope.launch {
            try {
                // Reset all app usage for today
                appUsageRepository.resetAllUsage()
                // Reset daily statistics
                dailyStatisticsRepository.resetTodayStatistics()
                // Reload dashboard data
                loadDashboardData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun hasUsageStatsPermission(): Boolean {
        return usageTrackingService.hasUsageStatsPermission()
    }
} 