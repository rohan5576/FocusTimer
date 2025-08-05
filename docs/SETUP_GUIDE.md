# 🚀 MindGuard Setup Guide

## Quick Start Checklist

### ✅ **Step 1: Install the App**
```bash
# Clone and build
git clone https://github.com/your-username/mindguard.git
cd mindguard
./gradlew installDebug
```

### ✅ **Step 2: Enable Critical Permissions**

#### **🔧 Accessibility Service** (CRITICAL)
1. Open **Settings** → **Accessibility**
2. Find **MindGuard** in the list
3. Toggle **ON**
4. Confirm in the dialog

**Service Path**: `com.mindguard.app/.service.MindGuardAccessibilityService`

#### **📊 Usage Access Permission** (CRITICAL)
1. Open **Settings** → **Apps & notifications**
2. Select **Special app access**
3. Choose **Usage access**
4. Find **MindGuard** → Toggle **Allow**

#### **🖼️ Display over other apps** (CRITICAL)
1. Open **Settings** → **Apps**
2. Select **MindGuard**
3. Choose **Display over other apps**
4. Toggle **Allow**

### ✅ **Step 3: Test Setup**

#### **Quick Test via Debug Screen**
1. Open MindGuard app
2. Navigate to **Debug** tab (if available)
3. Check permission status:
   - ✅ Accessibility Service: ENABLED
   - ✅ Usage Access: GRANTED
   - ✅ Overlay Permission: GRANTED
4. Click **"Test Overlay Capability"**
5. Should see test overlay for 2 seconds

#### **Full Test with Real App**
1. Click **"Create Test App (Instagram - 1min limit)"**
2. Exit MindGuard and open **Instagram**
3. **Expected**: Blocking overlay appears with math challenge
4. **If not working**: Check logs and permissions

### 🐛 **Troubleshooting**

#### **Overlay Not Appearing**
```
Problem: Math dialog doesn't show when time limit exceeded
Solutions:
1. Verify ALL permissions are granted
2. Check accessibility service is actually enabled
3. Use Debug → Test Overlay Capability
4. Check Logcat for error messages
5. Restart device if needed
```

#### **App Not Being Monitored**
```
Problem: Apps don't trigger time limit checks
Solutions:
1. Ensure accessibility service is running
2. Check if app package name is correct
3. Verify app has usage data (open it first)
4. Check repository data is saving correctly
```

#### **Permissions Keep Getting Disabled**
```
Problem: Android disables permissions automatically
Solutions:
1. Disable battery optimization for MindGuard
2. Add MindGuard to "Never sleeping apps"
3. Ensure foreground service is running
4. Check device power management settings
```

### 📱 **Device-Specific Notes**

#### **Samsung Devices**
- May need to disable **"Put app to sleep"**
- Check **"Apps that won't be put to sleep"**
- Verify **"Auto-start"** is enabled

#### **Xiaomi/MIUI**
- Enable **"Autostart"** for MindGuard
- Disable **"Battery saver"** for app
- Check **"Other permissions"** settings

#### **OnePlus/OxygenOS**
- Disable **"Advanced optimization"**
- Enable **"Allow background activity"**
- Check **"App auto-launch"** settings

### 🔍 **Debug Information**

#### **Accessibility Service Status**
```kotlin
// Check if service is running
adb shell settings get secure enabled_accessibility_services
// Should include: com.mindguard.app/.service.MindGuardAccessibilityService
```

#### **Overlay Permission Status**
```kotlin
// Check overlay permission
adb shell appops get com.mindguard.app SYSTEM_ALERT_WINDOW
// Should return: allow
```

#### **Usage Stats Permission**
```kotlin
// Check usage stats permission  
adb shell appops get com.mindguard.app GET_USAGE_STATS
// Should return: allow
```

### 📋 **Common Logcat Filters**

```bash
# MindGuard general logs
adb logcat | grep "MindGuard"

# Accessibility service logs
adb logcat | grep "MindGuardAccessibility"

# Overlay specific logs
adb logcat | grep "showBlockingOverlay"

# Permission checks
adb logcat | grep "ACCESSIBILITY DEBUG"
```

### ⚡ **Performance Tips**

1. **Battery Optimization**: Disable for MindGuard
2. **Memory Management**: Allow background activity
3. **Auto-start**: Enable for consistent monitoring
4. **Network**: Required for online quotes only

---

**Need Help?** Check the main README.md or create an issue on GitHub.