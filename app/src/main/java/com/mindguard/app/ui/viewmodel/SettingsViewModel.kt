package com.mindguard.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.app.data.model.ProblemDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val difficultyLevel: ProblemDifficulty = ProblemDifficulty.MEDIUM,
    val notificationsEnabled: Boolean = true,
    val hasAccessibilityPermission: Boolean = false,
    val hasUsageStatsPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDifficultyDialog: Boolean = false
)

class SettingsViewModel(
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        checkPermissions()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val sharedPrefs = context.getSharedPreferences("mindguard_settings", Context.MODE_PRIVATE)
                
                val difficulty = when (sharedPrefs.getString("difficulty_level", "MEDIUM")) {
                    "EASY" -> ProblemDifficulty.EASY
                    "HARD" -> ProblemDifficulty.HARD
                    else -> ProblemDifficulty.MEDIUM
                }
                
                val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
                
                _uiState.value = _uiState.value.copy(
                    difficultyLevel = difficulty,
                    notificationsEnabled = notificationsEnabled
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    private fun checkPermissions() {
        viewModelScope.launch {
            try {
                val hasAccessibility = isAccessibilityServiceEnabled()
                val hasUsageStats = hasUsageStatsPermission()
                val hasOverlay = hasOverlayPermission()
                
                _uiState.value = _uiState.value.copy(
                    hasAccessibilityPermission = hasAccessibility,
                    hasUsageStatsPermission = hasUsageStats,
                    hasOverlayPermission = hasOverlay
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun openDifficultySettings() {
        _uiState.value = _uiState.value.copy(showDifficultyDialog = true)
    }
    
    fun dismissDifficultyDialog() {
        _uiState.value = _uiState.value.copy(showDifficultyDialog = false)
    }
    
    fun setDifficulty(difficulty: ProblemDifficulty) {
        viewModelScope.launch {
            try {
                val sharedPrefs = context.getSharedPreferences("mindguard_settings", Context.MODE_PRIVATE)
                sharedPrefs.edit()
                    .putString("difficulty_level", difficulty.name)
                    .apply()
                
                _uiState.value = _uiState.value.copy(
                    difficultyLevel = difficulty,
                    showDifficultyDialog = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleNotifications() {
        viewModelScope.launch {
            try {
                val newValue = !_uiState.value.notificationsEnabled
                
                val sharedPrefs = context.getSharedPreferences("mindguard_settings", Context.MODE_PRIVATE)
                sharedPrefs.edit()
                    .putBoolean("notifications_enabled", newValue)
                    .apply()
                
                _uiState.value = _uiState.value.copy(notificationsEnabled = newValue)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    
    fun openUsageStatsSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    
    fun openOverlaySettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    
    fun refreshPermissions() {
        checkPermissions()
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
        
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val serviceName = "${context.packageName}/${context.packageName}.service.MindGuardAccessibilityService"
            return services?.contains(serviceName) == true
        }
        
        return false
    }
    
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }
    
    private fun hasOverlayPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}