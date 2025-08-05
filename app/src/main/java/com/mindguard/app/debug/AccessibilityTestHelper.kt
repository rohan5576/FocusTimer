package com.mindguard.app.debug

import android.content.Context
import android.content.Intent
import android.provider.Settings

object AccessibilityTestHelper {

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityServiceName =
            "${context.packageName}/.service.MindGuardAccessibilityService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        return enabledServices?.contains(accessibilityServiceName) == true
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun openOverlaySettings(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${context.packageName}")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun logAccessibilityStatus(context: Context) {
        val accessibilityEnabled = isAccessibilityServiceEnabled(context)
        val overlayEnabled = hasOverlayPermission(context)

        android.util.Log.d("MindGuard", "=== ACCESSIBILITY DEBUG ===")
        android.util.Log.d("MindGuard", "Accessibility Service Enabled: $accessibilityEnabled")
        android.util.Log.d("MindGuard", "Overlay Permission Granted: $overlayEnabled")
        android.util.Log.d("MindGuard", "Package Name: ${context.packageName}")
        android.util.Log.d(
            "MindGuard",
            "Service Path: ${context.packageName}/.service.MindGuardAccessibilityService"
        )

        if (!accessibilityEnabled) {
            android.util.Log.w("MindGuard", "⚠️ Accessibility service is NOT enabled!")
        }

        if (!overlayEnabled) {
            android.util.Log.w("MindGuard", "⚠️ Overlay permission is NOT granted!")
        }

        if (accessibilityEnabled && overlayEnabled) {
            android.util.Log.d("MindGuard", "✅ All permissions granted - overlay should work!")
        }
    }
}