package com.mindguard.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.app.data.model.AppUsage
import com.mindguard.app.data.repository.AppUsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppsUiState(
    val monitoredApps: List<AppUsage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AppsViewModel(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                appUsageRepository.getAllEnabledApps().collect { apps ->
                    _uiState.value = _uiState.value.copy(
                        monitoredApps = apps, isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message, isLoading = false
                )
            }
        }
    }

    fun addApp(packageName: String, appName: String) {
        viewModelScope.launch {
            try {
                // Check if app already exists and reset its data
                val existingApp = appUsageRepository.getAppByPackage(packageName)
                if (existingApp != null) {
                    val resetApp = existingApp.copy(
                        currentUsage = 0,
                        dailyTimeLimit = 30,
                        isEnabled = true,
                        lastResetDate = System.currentTimeMillis()
                    )
                    appUsageRepository.updateApp(resetApp)
                } else {
                    val newApp = AppUsage(
                        packageName = packageName,
                        appName = appName,
                        dailyTimeLimit = 30, // Default 30 minutes
                        currentUsage = 0,
                        isEnabled = true,
                        lastResetDate = System.currentTimeMillis()
                    )
                    appUsageRepository.insertApp(newApp)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateApp(appUsage: AppUsage) {
        viewModelScope.launch {
            try {
                appUsageRepository.updateApp(appUsage)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteApp(appUsage: AppUsage) {
        viewModelScope.launch {
            try {
                // Completely delete the app from database
                appUsageRepository.deleteApp(appUsage)
                // Also clear any cached data for this package
                appUsageRepository.clearAppData(appUsage.packageName)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun toggleApp(appUsage: AppUsage) {
        viewModelScope.launch {
            try {
                val updatedApp = appUsage.copy(isEnabled = !appUsage.isEnabled)
                appUsageRepository.updateApp(updatedApp)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearAllApps() {
        viewModelScope.launch {
            try {
                appUsageRepository.clearAllApps()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    // Additional utility methods
    fun resetAllUsage() {
        viewModelScope.launch {
            try {
                appUsageRepository.resetAllUsage()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun getAppsAtLimit() = appUsageRepository.getAppsAtLimit()

    fun getAllApps() = appUsageRepository.getAllApps()
} 