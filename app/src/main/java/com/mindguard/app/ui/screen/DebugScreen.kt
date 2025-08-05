package com.mindguard.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindguard.app.MindGuardApplication
import com.mindguard.app.debug.AccessibilityTestHelper
import com.mindguard.app.service.UsageTrackingServiceNew
import com.mindguard.app.ui.viewmodel.AppsViewModel
import com.mindguard.app.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    appsViewModel: AppsViewModel = viewModel(
        factory = ViewModelFactory(
            (LocalContext.current.applicationContext as MindGuardApplication).appUsageRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).motivationalQuoteRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).dailyStatisticsRepository,
            LocalContext.current
        )
    )
) {
    val context = LocalContext.current
    val app = context.applicationContext as MindGuardApplication
    val appsState by appsViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var accessibilityEnabled by remember { mutableStateOf(false) }
    var overlayEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        accessibilityEnabled = AccessibilityTestHelper.isAccessibilityServiceEnabled(context)
        overlayEnabled = AccessibilityTestHelper.hasOverlayPermission(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🐛 DEBUG SCREEN",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Use this to fix the blocking overlay issue",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Permission Status
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔐 Permission Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Accessibility Service:")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (accessibilityEnabled) "✅ ENABLED" else "❌ DISABLED",
                                color = if (accessibilityEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            if (!accessibilityEnabled) {
                                IconButton(
                                    onClick = {
                                        AccessibilityTestHelper.openAccessibilitySettings(
                                            context
                                        )
                                    }) {
                                    Icon(
                                        Icons.Default.Settings, contentDescription = "Open Settings"
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overlay Permission:")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (overlayEnabled) "✅ GRANTED" else "❌ DENIED",
                                color = if (overlayEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            if (!overlayEnabled) {
                                IconButton(
                                    onClick = { AccessibilityTestHelper.openOverlaySettings(context) }) {
                                    Icon(
                                        Icons.Default.Settings, contentDescription = "Open Settings"
                                    )
                                }
                            }
                        }
                    }

                    if (!accessibilityEnabled || !overlayEnabled) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "⚠️ Blocking overlay won't work without these permissions!",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Overlay Test
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧪 Overlay Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val success =
                                com.mindguard.app.util.OverlayTestHelper.testOverlayCapability(
                                    context
                                )
                            if (success) {
                                android.widget.Toast.makeText(
                                    context,
                                    "✅ Overlay test successful!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ Overlay test failed! Check permissions.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Overlay Capability")
                    }

                    Text(
                        text = "This will show a test overlay for 2 seconds to verify overlay functionality.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Test App Creation
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🧪 Test Blocking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    // Create a test app with very low limit
                                    val testApp = com.mindguard.app.data.model.AppUsage(
                                        packageName = "com.instagram.android",
                                        appName = "Instagram Test",
                                        dailyTimeLimit = 1, // 1 minute limit
                                        currentUsage = 5, // Already over limit
                                        isEnabled = true
                                    )
                                    app.appUsageRepository.insertApp(testApp)
                                    android.util.Log.d(
                                        "MindGuard",
                                        "✅ Test app created with 1min limit and 5min usage"
                                    )
                                } catch (e: Exception) {
                                    android.util.Log.e("MindGuard", "❌ Error creating test app", e)
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Test App (Instagram - 1min limit)")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val usageService =
                                        UsageTrackingServiceNew(context, app.appUsageRepository)
                                    usageService.updateMonitoredAppsUsage()
                                    android.util.Log.d("MindGuard", "✅ Usage data updated")
                                } catch (e: Exception) {
                                    android.util.Log.e("MindGuard", "❌ Error updating usage", e)
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Usage Data")
                    }

                    Button(
                        onClick = {
                            AccessibilityTestHelper.logAccessibilityStatus(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Log Debug Info")
                    }

                    Text(
                        text = "💡 Steps to test:\n1. Enable accessibility service\n2. Grant overlay permission\n3. Create test app\n4. Open Instagram\n5. Overlay should appear!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Monitored Apps Status
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📱 Monitored Apps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (appsState.monitoredApps.isEmpty()) {
                        Text(
                            text = "No apps being monitored",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        appsState.monitoredApps.forEach { app ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(app.appName, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${app.currentUsage}/${app.dailyTimeLimit}m")
                                    Text(
                                        text = if (app.currentUsage >= app.dailyTimeLimit) "🚨 OVER LIMIT" else "✅ OK",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (app.currentUsage >= app.dailyTimeLimit) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Add to MainActivity navigation:
 * composable("debug") { DebugScreen() }
 */