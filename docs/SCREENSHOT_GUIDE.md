# 📸 Screenshot Guide for MindGuard

## Required Screenshots for README

### 1. **Dashboard Screen** (`dashboard.png`)
**What to show:**
- Main statistics card with real usage data
- Quick action buttons (Add Apps, Settings, Statistics)
- Today's progress with meaningful numbers
- List of monitored apps with current usage

**How to capture:**
1. Add 2-3 apps to monitoring
2. Let them accumulate some usage data
3. Ensure statistics show real numbers (not 0s)
4. Take screenshot in light mode for better visibility

### 2. **Apps Management Screen** (`apps_management.png`)
**What to show:**
- List of monitored apps with icons and usage
- "Add App" dialog with real installed apps
- App limit settings dialog
- Toggle switches for enabling/disabling apps

**Capture process:**
1. Navigate to Apps tab
2. Show at least 3 monitored apps
3. Optionally: Show Add App dialog overlay

### 3. **Blocking Overlay** (`blocking_overlay.png`)
**What to show:**
- Full-screen blocking overlay when time limit reached
- "Solve Problem" and "Take Break" buttons
- Clean, professional overlay design

**How to get this:**
1. Use Debug Screen → Create Test App (Instagram)
2. Open Instagram to trigger overlay
3. Take screenshot immediately when overlay appears
4. Ensure overlay is clearly visible

### 4. **Math Challenge Dialog** (`math_challenge.png`)
**What to show:**
- Arithmetic problem dialog overlay
- Math question clearly visible
- Answer input field and buttons
- Professional dialog design

**Capture method:**
1. Trigger blocking overlay first
2. Click "Solve Problem" button
3. Screenshot the math dialog
4. Ensure math problem is readable

### 5. **Statistics Screen** (`statistics.png`)
**What to show:**
- Weekly usage trends (bar chart)
- Achievement progress bars
- Real statistics (problems solved, breaks taken)
- Modern card-based layout

**Setup:**
1. Navigate to Statistics tab
2. Ensure some achievements have progress
3. Show meaningful data (not all zeros)

### 6. **Settings & Permissions** (`settings.png`)
**What to show:**
- Permission status (all green/granted)
- Difficulty level settings
- General app preferences
- Clean settings layout

**Requirements:**
1. Show all permissions as granted (green checkmarks)
2. Display different difficulty options
3. Show toggle switches and preferences

## Screenshot Specifications

### **Technical Requirements:**
- **Resolution**: 1080x2340 (standard Android)
- **Format**: PNG for clarity
- **Quality**: High resolution, crisp text
- **Mode**: Light theme for better GitHub visibility

### **Composition Guidelines:**
- **Clean UI**: No debug overlays or development artifacts
- **Real Data**: Show actual usage numbers, not placeholders
- **Professional**: Ensure app looks polished and complete
- **Context**: Each screenshot should tell a story about the feature

## Automated Screenshot Tool

### **Using Android Studio:**
```bash
# Connect device and run:
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png ./screenshots/
```

### **Using Gradle Task:**
```kotlin
// Add to app/build.gradle
task captureScreenshots {
    doLast {
        // Automated screenshot capture logic
    }
}
```

## File Organization

```
docs/
├── screenshots/
│   ├── dashboard.png
│   ├── apps_management.png
│   ├── blocking_overlay.png
│   ├── math_challenge.png
│   ├── statistics.png
│   ├── settings.png
│   └── app_icon.png
└── README.md (update with image links)
```

## Image Optimization

### **Before Adding to Git:**
1. **Compress images** using tools like TinyPNG
2. **Resize if needed** - max width 800px for GitHub
3. **Optimize file size** - aim for <500KB per image
4. **Verify clarity** - text should be readable

### **GitHub Markdown Usage:**
```markdown
## Screenshots

### Main Dashboard
![Dashboard](docs/screenshots/dashboard.png)
*Real-time usage statistics and quick actions*

### App Management
![Apps Management](docs/screenshots/apps_management.png)
*Add, remove, and configure app monitoring settings*

### Blocking Overlay
![Blocking Overlay](docs/screenshots/blocking_overlay.png)
*Math challenge when time limit is reached*
```

## Quality Checklist

### ✅ **Before Publishing:**
- [ ] All screenshots show real, not dummy data
- [ ] UI is clean and professional looking
- [ ] Text is clearly readable
- [ ] Images are properly compressed
- [ ] File names are descriptive and consistent
- [ ] Screenshots represent current app version
- [ ] No personal/sensitive information visible
- [ ] All key features are represented

---

**Professional screenshots are crucial for showcasing your Android development skills!**