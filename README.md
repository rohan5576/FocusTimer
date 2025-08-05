# 🧠 FocusTimer(MindGuard) - Digital Wellness & App Blocker

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-orange.svg)](https://m3.material.io)
[![Room](https://img.shields.io/badge/Database-Room-red.svg)](https://developer.android.com/training/data-storage/room)

**MindGuard** is a sophisticated digital wellness application that helps users develop healthier smartphone habits through **intentional friction** and **cognitive challenges**. Unlike simple app blockers, MindGuard uses intelligent intervention methods to encourage mindful usage.

## 📱 Features

### 🎯 **Core Functionality**
- **📊 Real-time App Monitoring** - Track usage of social media and addictive apps
- **⏰ Smart Time Limits** - Set daily usage limits for each monitored app
- **🧮 Arithmetic Challenges** - Solve math problems to earn extra app time
- **🛡️ Blocking Overlay** - Full-screen overlay when time limits are exceeded
- **📈 Usage Statistics** - Comprehensive analytics and progress tracking
- **💡 Motivational Quotes** - Daily inspiration from online APIs

### 🔧 **Advanced Features**
- **🔄 Usage Reset** - Clean data management when removing/re-adding apps
- **🎚️ Difficulty Levels** - Easy, Medium, Hard math problems
- **📊 Weekly Trends** - Visual progress tracking over time
- **🏆 Achievements** - Gamified progress tracking
- **🌐 Online Content** - Fresh quotes from external APIs
- **🐛 Debug Tools** - Comprehensive debugging and testing utilities

## 📱 Screenshots
  
<img src="screenshot/Dashboard.png" alt="Light Mode" width="300"/>

<img src="screenshot/MontioredApp.png" alt="Light Mode Detail" width="300"/>

<img src="screenshot/AddApps.png" alt="Light Mode Detail" width="300"/>

<img src="screenshot/Quotes.png" alt="Light Mode Detail" width="300"/>

<img src="screenshot/Settings.png" alt="Light Mode Detail" width="300"/>


## 🏗️ Technical Architecture

### **🛠️ Android Components & Libraries Used**

#### **UI & Design**
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3 Design System** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation between screens
- **Custom Theming** - Dynamic colors, gradients, and spacing
- **Responsive Layout** - Adaptive UI for different screen sizes

#### **Data & Storage**
- **Room Persistence Library** - Local SQLite database management
- **Flow & StateFlow** - Reactive data streams
- **Repository Pattern** - Clean data layer architecture
- **Data Classes** - Immutable data structures

#### **Background Services**
- **Accessibility Service** - App usage monitoring and blocking
- **Foreground Service** - Continuous usage tracking
- **WorkManager** - Scheduled background tasks
- **UsageStatsManager** - System-level app usage data

#### **Permissions & System Integration**
- **PACKAGE_USAGE_STATS** - Access app usage statistics
- **SYSTEM_ALERT_WINDOW** - Display overlay windows
- **BIND_ACCESSIBILITY_SERVICE** - Monitor app switches
- **INTERNET & NETWORK_STATE** - Fetch online content

#### **Architecture Components**
- **ViewModel** - UI state management and business logic
- **LiveData/StateFlow** - Observable data holders
- **Dependency Injection** - Manual DI with repository pattern
- **MVVM Architecture** - Clean separation of concerns

#### **Networking & APIs**
- **Retrofit** - REST API client for quotes
- **Kotlin Coroutines** - Asynchronous programming
- **JSON Parsing** - Kotlinx Serialization
- **Error Handling** - Robust fallback mechanisms

#### **Advanced Android Features**
- **WindowManager** - Overlay window management
- **PackageManager** - Installed app discovery
- **SharedPreferences** - User settings persistence
- **Intent System** - Deep linking and system settings
- **Accessibility APIs** - App behavior monitoring

## 📦 Project Structure

```
app/src/main/java/com/mindguard/app/
├── 📁 data/
│   ├── 🗄️ database/           # Room database, DAOs
│   ├── 📊 model/              # Data classes and entities
│   ├── 🌐 remote/             # API services and networking
│   └── 📦 repository/         # Data repository implementations
├── 🎨 ui/
│   ├── 🧩 component/          # Reusable UI components
│   ├── 📱 screen/             # App screens and dialogs
│   ├── 🎭 theme/              # Material 3 theming
│   └── 🎯 viewmodel/          # ViewModel classes
├── 🔧 service/                # Background services
├── 🛠️ util/                  # Utility classes and helpers
├── 🐛 debug/                  # Debug and testing tools
└── 📱 MainActivity.kt         # Main entry point
```

## 🚀 Installation & Setup

### **Prerequisites**
- Android 7.0 (API level 24) or higher
- Kotlin 1.9+
- Android Studio Hedgehog or newer

### **Build Instructions**

1. **Clone the repository**
```bash
git clone https://github.com/rohan5576/FocusTimer.git
cd FocusTimer
```

2. **Open in Android Studio**
```bash
# Open the project in Android Studio
# or use command line:
./gradlew build
```

3. **Install on device**
```bash
./gradlew installDebug
```

### **Required Permissions Setup**

After installation, grant these critical permissions:

#### 1. **Accessibility Service**
```
Settings → Accessibility → MindGuard → Enable
```

#### 2. **Usage Access Permission**
```
Settings → Apps & notifications → Special app access → Usage access → MindGuard → Allow
```

#### 3. **Display over other apps**
```
Settings → Apps → MindGuard → Display over other apps → Allow
```

## 🧪 Testing & Debugging

### **Debug Features**
- **Overlay Test Tool** - Verify overlay functionality
- **Permission Checker** - Real-time permission status
- **Test App Creator** - Create test scenarios
- **Comprehensive Logging** - Detailed debug information

### **Testing Workflow**
1. Enable all required permissions
2. Use Debug Screen to test overlay capability
3. Create test app (Instagram with 1-minute limit)
4. Open Instagram to trigger blocking overlay
5. Verify math challenge appears correctly

## 🔧 Configuration

### **App Settings**
- **Difficulty Level**: Easy (addition), Medium (multiplication), Hard (complex operations)
- **Notification Preferences**: Enable/disable alerts
- **Auto-refresh**: Automatic quote updates

### **Monitored Apps**
- Add any installed application
- Set custom daily time limits (in minutes)
- Enable/disable monitoring per app
- Reset usage data when needed

### **Storage**
- Local SQLite database via Room
- No data transmitted to external servers
- User privacy and data ownership maintained

## 🎯 Key Android Skills Demonstrated

### **🏛️ Architecture & Design Patterns**
- **MVVM Architecture** with clean separation
- **Repository Pattern** for data management
- **Observer Pattern** with Flow/StateFlow
- **Dependency Injection** implementation

### **🎨 Modern UI Development**
- **Jetpack Compose** declarative UI
- **Material 3** design system implementation
- **Custom theming** and component design
- **Responsive layouts** and navigation

### **⚡ Performance & Optimization**
- **Efficient database queries** with Room
- **Background processing** with coroutines
- **Memory management** in services
- **Battery optimization** considerations

### **🔐 Security & Permissions**
- **Runtime permission handling**
- **Accessibility service security**
- **Overlay permission management**
- **Privacy-first data handling**

### **🔧 System Integration**
- **Deep system API usage** (UsageStats, Accessibility)
- **Service lifecycle management**
- **Window management** for overlays
- **Intent system** and deep linking

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit changes** (`git commit -m 'Add amazing feature'`)
4. **Push to branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### **Code Style**
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Maintain consistent formatting


**Built with ❤️ using Modern Android Development practices**

*FocusTimer - Empowering mindful digital habits through intelligent intervention*
