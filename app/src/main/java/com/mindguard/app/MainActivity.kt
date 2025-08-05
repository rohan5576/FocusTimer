package com.mindguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindguard.app.ui.screen.DashboardScreen
import com.mindguard.app.ui.screen.SettingsScreen
import com.mindguard.app.ui.screen.AppsScreen
import com.mindguard.app.ui.screen.QuotesScreen
import com.mindguard.app.ui.screen.StatisticsScreen
import com.mindguard.app.ui.screen.DebugScreen
import com.mindguard.app.ui.theme.MindGuardTheme
import com.mindguard.app.ui.component.MainBottomNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindGuardApp()
                }
            }
        }
    }
}

@Composable
fun MindGuardApp() {
    val navController = rememberNavController()
    
    androidx.compose.material3.Scaffold(
        bottomBar = {
            MainBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController, 
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen(navController = navController)
            }
            composable("apps") {
                AppsScreen()
            }
            composable("quotes") {
                QuotesScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("statistics") {
                StatisticsScreen()
            }
           /* composable("debug") {
                DebugScreen()
            }*/
        }
    }
} 