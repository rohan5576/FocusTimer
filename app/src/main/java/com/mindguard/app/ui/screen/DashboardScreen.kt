package com.mindguard.app.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mindguard.app.MindGuardApplication
import com.mindguard.app.ui.theme.AppGradients
import com.mindguard.app.ui.theme.CornerRadius
import com.mindguard.app.ui.theme.IconSize
import com.mindguard.app.ui.theme.Spacing
import com.mindguard.app.ui.theme.gradientBackground
import com.mindguard.app.ui.viewmodel.DashboardViewModel
import com.mindguard.app.ui.viewmodel.ViewModelFactory
import java.util.Calendar

fun formatScreenTime(minutes: Long): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes == 0L) "${hours}h" else "${hours}h ${remainingMinutes}m"
        }

        else -> {
            val hours = minutes / 60
            "${hours}h+"
        }
    }
}

@Composable
fun getAppIconDashboard(packageName: String, context: android.content.Context): ImageBitmap? {
    return remember(packageName) {
        try {
            val packageManager = context.packageManager
            packageManager.getApplicationIcon(packageName).toBitmap(width = 128, height = 128)
                .asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController, viewModel: DashboardViewModel = viewModel(
        factory = ViewModelFactory(
            (LocalContext.current.applicationContext as MindGuardApplication).appUsageRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).motivationalQuoteRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).dailyStatisticsRepository,
            LocalContext.current
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                horizontal = Spacing.ScreenPadding, vertical = Spacing.Medium
            ), verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing)
        ) {
            item {
                WelcomeHeader(onRefresh = { viewModel.refreshData() })
            }

            item {
                ModernStatisticsCard(
                    totalScreenTime = uiState.totalScreenTime,
                    problemsSolved = uiState.problemsSolved,
                    breaksTaken = uiState.breaksTaken,
                    timeSaved = uiState.timeSaved,
                    isLoading = uiState.isLoading
                )
            }

            item {
                QuickActionsCard(navController = navController)
            }

            item {
                SectionHeader(
                    title = "Monitored Apps",
                    subtitle = "${uiState.monitoredApps.size} apps being tracked"
                )
            }

            if (uiState.monitoredApps.isNotEmpty()) {
                items(uiState.monitoredApps) { appUsage ->
                    ModernAppUsageCard(appUsage = appUsage)
                }
            } else {
                item {
                    ModernEmptyStateCard(navController = navController)
                }
            }
        }

        if (uiState.error != null) {
            LaunchedEffect(uiState.error) {
                // Show snackbar for errors
                println("Dashboard Error: ${uiState.error}")
            }
        }
    }
}

@Composable
fun WelcomeHeader(onRefresh: () -> Unit = {}) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Stay focused and mindful today",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ModernStatisticsCard(
    totalScreenTime: Long,
    problemsSolved: Int,
    breaksTaken: Int,
    timeSaved: Long,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XXXLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .gradientBackground(
                    AppGradients.primaryGradient(), alpha = 0.08f
                )
                .padding(Spacing.ExtraLarge)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Progress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        CircularProgressRing(
                            progress = if (totalScreenTime > 0) (timeSaved.toFloat() / (totalScreenTime + timeSaved)) else 0f,
                            size = 48.dp,
                            strokeWidth = 6.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            ModernStatItem(
                                icon = Icons.Default.Timer,
                                label = "Screen Time",
                                value = "${totalScreenTime}min",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        item {
                            ModernStatItem(
                                icon = Icons.Default.Psychology,
                                label = "Problems",
                                value = problemsSolved.toString(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        item {
                            ModernStatItem(
                                icon = Icons.Default.SelfImprovement,
                                label = "Breaks",
                                value = breaksTaken.toString(),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        item {
                            ModernStatItem(
                                icon = Icons.Default.TrendingUp,
                                label = "Time Saved",
                                value = "${timeSaved}min",
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.size(width = 100.dp, height = 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(IconSize.Medium)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CircularProgressRing(
    progress: Float,
    size: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val canvasSize = this.size.minDimension
        val strokeWidthPx = strokeWidth.toPx()
        val radius = (canvasSize - strokeWidthPx) / 2
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2

        // Background circle
        drawCircle(
            color = Color.Gray.copy(alpha = 0.3f),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Progress arc
        drawArc(
            color = Color(0xFF4CAF50),
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - radius, centerY - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

@Composable
fun QuickActionsCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XXLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .gradientBackground(
                    AppGradients.primaryGradient(), alpha = 0.03f
                )
                .padding(Spacing.ExtraLarge)
        ) {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.Medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        label = "Add App",
                        onClick = { navController.navigate("apps") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.Analytics,
                        label = "Statistics",
                        onClick = { navController.navigate("statistics") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String, subtitle: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ModernAppUsageCard(appUsage: com.mindguard.app.data.model.AppUsage) {
    val progress =
        (appUsage.currentUsage.toFloat() / appUsage.dailyTimeLimit.toFloat()).coerceIn(0f, 1f)
    val isOverLimit = progress >= 1f
    val remainingTime = (appUsage.dailyTimeLimit - appUsage.currentUsage).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverLimit) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // App Icon
                        val context = LocalContext.current
                        val appIcon = getAppIconDashboard(appUsage.packageName, context)

                        if (appIcon != null) {
                            Image(
                                bitmap = appIcon,
                                contentDescription = "${appUsage.appName} icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        } else {
                            // Fallback to colored circle if icon loading fails
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = appUsage.appName.take(2).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = appUsage.appName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Status indicator
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isOverLimit) MaterialTheme.colorScheme.error
                                        else if (progress > 0.8f) Color(0xFFFF9800)
                                        else MaterialTheme.colorScheme.primary
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${appUsage.currentUsage} / ${appUsage.dailyTimeLimit} minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!isOverLimit) {
                        Text(
                            text = "${remainingTime} minutes remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "Limit exceeded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                CircularProgressRing(
                    progress = progress, size = 56.dp, strokeWidth = 4.dp
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isOverLimit) MaterialTheme.colorScheme.error
                else if (progress > 0.8f) Color(0xFFFF9800)
                else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun ModernEmptyStateCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Apps Monitored",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add apps to start tracking your digital wellness journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("apps") }, shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your First App")
            }
        }
    }
} 