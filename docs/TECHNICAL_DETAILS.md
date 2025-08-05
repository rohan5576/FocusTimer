# 🔧 Technical Implementation Details

## 🏗️ Architecture Overview

### **Design Pattern: MVVM + Repository**
```
┌─ UI Layer (Jetpack Compose)
│  ├─ Screens & ViewModels
│  └─ Navigation & Theme
│
├─ Domain Layer
│  ├─ Use Cases
│  └─ Business Logic
│
└─ Data Layer
   ├─ Repository Pattern
   ├─ Room Database
   └─ Remote APIs
```

## 🔐 Accessibility Service Implementation

### **Core Monitoring Logic**
```kotlin
class MindGuardAccessibilityService : AccessibilityService() {
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString()
                checkAppUsage(packageName)
            }
        }
    }
    
    private fun checkAppUsage(packageName: String) {
        // 1. Query monitored apps from database
        // 2. Update current usage from UsageStatsManager
        // 3. Compare against daily time limit
        // 4. Show blocking overlay if exceeded
    }
}
```

### **Accessibility Service Configuration**
```xml
<accessibility-service 
    android:accessibilityEventTypes="typeWindowStateChanged"
    android:accessibilityFlags="flagReportViewIds"
    android:canRetrieveWindowContent="true"
    android:notificationTimeout="100" />
```

## 🖼️ Overlay System Implementation

### **WindowManager Integration**
```kotlin
private fun showBlockingOverlay() {
    val params = WindowManager.LayoutParams().apply {
        width = MATCH_PARENT
        height = MATCH_PARENT
        type = TYPE_APPLICATION_OVERLAY
        flags = FLAG_NOT_FOCUSABLE or FLAG_LAYOUT_IN_SCREEN
        format = PixelFormat.TRANSLUCENT
    }
    
    windowManager.addView(overlayView, params)
}
```

### **Permission Requirements**
- **SYSTEM_ALERT_WINDOW**: Draw over other apps
- **BIND_ACCESSIBILITY_SERVICE**: Monitor app switches
- **PACKAGE_USAGE_STATS**: Access usage statistics

## 📊 Database Schema

### **Room Database Structure**
```kotlin
@Entity(tableName = "app_usage")
data class AppUsage(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyTimeLimit: Long,        // Minutes
    val currentUsage: Long,          // Minutes
    val isEnabled: Boolean,
    val lastResetDate: Long          // Timestamp
)

@Entity(tableName = "daily_statistics") 
data class DailyStatistics(
    @PrimaryKey val date: String,    // YYYY-MM-DD
    val totalScreenTime: Long,
    val problemsSolved: Int,
    val breaksTaken: Int,
    val timeSaved: Long
)
```

### **Key Database Operations**
```kotlin
@Dao
interface AppUsageDao {
    @Query("SELECT * FROM app_usage WHERE isEnabled = 1")
    fun getAllEnabledApps(): Flow<List<AppUsage>>
    
    @Query("UPDATE app_usage SET currentUsage = :usage WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, usage: Long)
    
    @Query("SELECT * FROM app_usage WHERE currentUsage >= dailyTimeLimit")
    fun getAppsAtLimit(): Flow<List<AppUsage>>
}
```

## ⏱️ Real-time Usage Tracking

### **UsageStatsManager Integration**
```kotlin
class UsageTrackingServiceNew {
    fun getTodayScreenTime(): Long {
        val usageStats = usageStatsManager.queryUsageStats(
            INTERVAL_DAILY,
            getStartOfDayMillis(),
            System.currentTimeMillis()
        )
        
        return usageStats.sumOf { 
            it.totalTimeInForeground / (1000 * 60) // Convert to minutes
        }
    }
}
```

### **Foreground Service for Continuous Monitoring**
```kotlin
class AppUsageUpdateService : Service() {
    private fun startPeriodicUpdates() {
        serviceScope.launch {
            while (isActive) {
                usageTrackingService.updateMonitoredAppsUsage()
                delay(30000) // Update every 30 seconds
            }
        }
    }
}
```

## 🧮 Arithmetic Challenge System

### **Problem Generation**
```kotlin
object ArithmeticProblemGenerator {
    fun generateProblem(difficulty: ProblemDifficulty): ArithmeticProblem {
        return when (difficulty) {
            EASY -> generateAddition()
            MEDIUM -> generateMultiplication() 
            HARD -> generateComplexOperation()
        }
    }
}

data class ArithmeticProblem(
    val question: String,
    val answer: Int,
    val extraTimeReward: Long // Minutes earned for correct answer
)
```

## 🌐 Network Integration

### **Online Quote Fetching**
```kotlin
interface QuoteApiService {
    @GET("random")
    suspend fun fetchRandomQuote(): QuoteResponse
    
    @GET("quotes")
    suspend fun fetchMultipleQuotes(@Query("limit") limit: Int): List<QuoteResponse>
}

class MotivationalQuoteRepository {
    suspend fun refreshQuotesFromOnline() {
        try {
            val onlineQuotes = quoteApiService.fetchMultipleQuotes(15)
            // Cache to local database with fallback
        } catch (e: Exception) {
            // Use offline fallback quotes
        }
    }
}
```

## 🎨 UI Architecture (Jetpack Compose)

### **State Management Pattern**
```kotlin
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }
    
    when {
        uiState.isLoading -> LoadingScreen()
        uiState.error != null -> ErrorScreen(uiState.error)
        else -> DashboardContent(uiState)
    }
}
```

### **Material 3 Theming**
```kotlin
@Composable
fun MindGuardTheme(content: @Composable () -> Unit) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## 🔄 Data Flow Architecture

### **Repository Pattern Implementation**
```kotlin
class AppUsageRepository(private val appUsageDao: AppUsageDao) {
    
    fun getAllEnabledApps(): Flow<List<AppUsage>> = appUsageDao.getAllEnabledApps()
    
    suspend fun updateApp(appUsage: AppUsage) = appUsageDao.updateApp(appUsage)
    
    suspend fun getAppByPackage(packageName: String): AppUsage? = 
        appUsageDao.getAppByPackage(packageName)
}
```

### **ViewModel State Management**
```kotlin
class DashboardViewModel(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    fun loadDashboardData() {
        viewModelScope.launch {
            appUsageRepository.getAllEnabledApps().collect { apps ->
                _uiState.value = _uiState.value.copy(
                    monitoredApps = apps,
                    isLoading = false
                )
            }
        }
    }
}
```

## 🛡️ Security Considerations

### **Permission Validation**
```kotlin
object SecurityHelper {
    fun validateAccessibilityPermission(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(context.packageName) == true
    }
    
    fun validateOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
```

### **Data Privacy**
- All data stored locally using Room
- No user data transmitted to external servers
- Usage statistics never leave the device
- Accessibility service only monitors app switches

## ⚡ Performance Optimizations

### **Database Optimization**
```kotlin
// Efficient queries with proper indexing
@Query("SELECT * FROM app_usage WHERE isEnabled = 1 AND currentUsage >= dailyTimeLimit")
fun getAppsAtLimitOptimized(): Flow<List<AppUsage>>

// Batch updates for better performance
@Transaction
suspend fun updateMultipleApps(apps: List<AppUsage>) {
    apps.forEach { updateApp(it) }
}
```

### **Memory Management**
```kotlin
class MindGuardAccessibilityService : AccessibilityService() {
    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
        windowManager = null
        overlayView = null
    }
}
```

### **Battery Optimization**
- Minimal background processing
- Efficient service lifecycle management
- Smart polling intervals
- Proper coroutine scoping

---

**This technical implementation showcases modern Android development practices and architectural patterns.**