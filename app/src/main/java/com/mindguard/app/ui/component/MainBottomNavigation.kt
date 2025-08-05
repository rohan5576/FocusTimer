package com.mindguard.app.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mindguard.app.R

@Composable
fun MainBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = "dashboard",
            icon = Icons.Default.Dashboard,
            labelResId = R.string.dashboard_title
        ),
        BottomNavItem(
            route = "apps",
            icon = Icons.Default.List,
            labelResId = R.string.apps_title
        ),
        BottomNavItem(
            route = "quotes",
            icon = Icons.Default.FormatQuote,
            labelResId = R.string.quotes_title
        ),
        BottomNavItem(
            route = "settings",
            icon = Icons.Default.Settings,
            labelResId = R.string.settings_title
        )
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                label = { Text(stringResource(item.labelResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelResId: Int
) 