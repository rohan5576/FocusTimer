# 🧪 OVERLAY TESTING INSTRUCTIONS

## 📱 Current Test Environment
- **Device**: Motorola Edge 50 Fusion
- **App Status**: ✅ Installed and Running
- **Permissions**: 
  - ✅ Overlay: GRANTED
  - ✅ Usage Stats: GRANTED  
  - ❌ Accessibility Service: NOT ENABLED

## 🎯 Step-by-Step Testing Process

### **Phase 1: Test Basic Overlay Capability**

#### **1.1 Navigate to Debug Screen**
```
1. Open MindGuard app (should already be running)
2. Look for "Debug" tab in bottom navigation
3. Tap on Debug tab
4. You should see permission status and test buttons
```

#### **1.2 Test Overlay Capability**
```
1. In Debug screen, look for "🧪 Overlay Test" card
2. Click "Test Overlay Capability" button
3. EXPECTED: You should see a red test overlay for 2 seconds with text:
   "🎯 OVERLAY TEST SUCCESS! This confirms overlay capability"
4. Toast should show: "✅ Overlay test successful!"
```

**If overlay appears**: ✅ Basic overlay system works!
**If no overlay**: ❌ Check logs for errors

---

### **Phase 2: Enable Accessibility Service**

#### **2.1 Check Current Status**
```
1. In Debug screen, check "Accessibility Service" status
2. Should show "❌ DENIED" with settings button
3. Click the settings button next to it
```

#### **2.2 Enable Accessibility Service**
```
1. Android Settings will open to Accessibility section
2. Find "MindGuard" in the list
3. Tap on "MindGuard"
4. Toggle the switch to ON
5. Confirm in the dialog that appears
6. Go back to MindGuard app
```

#### **2.3 Verify Service Enabled**
```
1. In Debug screen, refresh or check status again
2. Should now show "✅ GRANTED" for Accessibility Service
3. All three permissions should be green
```

---

### **Phase 3: Test Real App Blocking**

#### **3.1 Create Test App**
```
1. In Debug screen, find "Test App Creation" section
2. Click "Create Test App (Instagram - 1min limit)" button
3. This creates Instagram with 1-minute limit and 5 minutes usage
4. Should see success message
```

#### **3.2 Test Blocking Overlay**
```
1. Exit MindGuard app (press home button)
2. Open Instagram app on your device
3. EXPECTED: Blocking overlay should appear immediately with:
   - "Time Limit Reached" title
   - Description text
   - "Solve Problem" and "Take Break" buttons
4. Math dialog should auto-appear after 1.5 seconds
```

#### **3.3 Test Math Challenge**
```
1. When math dialog appears, check:
   - Question is clearly displayed (e.g., "7 × 8 = ?")
   - Answer input field is visible
   - "Submit" and "Take Break" buttons work
2. Try solving the math problem:
   - Enter correct answer → Should grant extra time
   - Enter wrong answer → Should show "Try again" message
3. Try "Take Break" button → Should close app and go back
```

---

### **Phase 4: Verify Logging**

#### **4.1 Expected Log Messages**
Monitor logs for these success indicators:
```
✅ "🔍 Checking app usage for: com.instagram.android"
✅ "✅ Found monitored app: Instagram"  
✅ "🧪 DEBUGGING: Force trigger for Instagram test"
✅ "🚨 TIME LIMIT EXCEEDED! Triggering overlay"
✅ "🔥 showBlockingOverlay called for com.instagram.android"
✅ "✅ OVERLAY SUCCESSFULLY ADDED TO WINDOW!"
✅ "🧮 Auto-showing math problem for testing"
```

#### **4.2 Error Indicators**
Watch for these error messages:
```
❌ "❌ OVERLAY PERMISSION NOT GRANTED!"
❌ "Failed to inflate overlay layout"
❌ "❌ CRITICAL: Failed to add overlay to window"
❌ "⚠️ Accessibility service is NOT enabled!"
```

---

## 🔍 Troubleshooting Guide

### **Problem: Test Overlay Doesn't Appear**
```
Solutions:
1. Check overlay permission: Settings → Apps → MindGuard → Display over other apps
2. Disable battery optimization for MindGuard
3. Check logs for specific error messages
4. Restart app and try again
```

### **Problem: Accessibility Service Won't Enable**
```
Solutions:
1. Go to Settings → Accessibility manually
2. Look for MindGuard in Downloaded services section
3. Force stop and restart MindGuard app
4. Check device-specific accessibility restrictions
```

### **Problem: Instagram Blocking Doesn't Work**
```
Solutions:
1. Verify accessibility service is enabled and running
2. Check that test Instagram app was created successfully
3. Ensure Instagram app is actually installed on device
4. Monitor logs for debugging information
```

### **Problem: Math Dialog Doesn't Appear**
```
Solutions:
1. Wait 1.5 seconds after blocking overlay appears
2. Check for layout inflation errors in logs
3. Verify overlay permission is properly granted
4. Try restarting the accessibility service
```

---

## ✅ Success Criteria

### **Basic Overlay Test**: 
- ✅ Red test overlay appears for 2 seconds
- ✅ Toast confirmation message shows

### **Full Blocking Test**:
- ✅ Blocking overlay appears when opening Instagram
- ✅ Math dialog appears automatically
- ✅ Can solve math problems and get extra time
- ✅ "Take Break" closes the app

### **Service Integration**:
- ✅ Accessibility service monitors app switches
- ✅ Database properly tracks app usage
- ✅ Overlay system works reliably

---

**🎯 Complete these tests to verify all overlay functionality is working correctly!**