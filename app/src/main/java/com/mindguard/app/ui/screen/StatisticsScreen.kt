package com.mindguard.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindguard.app.MindGuardApplication
import com.mindguard.app.ui.theme.AppGradients
import com.mindguard.app.ui.theme.CornerRadius
import com.mindguard.app.ui.theme.IconSize
import com.mindguard.app.ui.theme.Spacing
import com.mindguard.app.ui.theme.SuccessColor
import com.mindguard.app.ui.theme.WarningColor
import com.mindguard.app.ui.theme.gradientBackground
import com.mindguard.app.ui.viewmodel.DashboardViewModel
import com.mindguard.app.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: DashboardViewModel = viewModel(
        factory = ViewModelFactory(
            (LocalContext.current.applicationContext as MindGuardApplication).appUsageRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).motivationalQuoteRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).dailyStatisticsRepository,
            LocalContext.current
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing)
    ) {
        item {
            StatisticsHeader()
        }

        item {
            TodayOverviewCard(
                totalScreenTime = uiState.totalScreenTime,
                problemsSolved = uiState.problemsSolved,
                breaksTaken = uiState.breaksTaken,
                timeSaved = uiState.timeSaved
            )
        }

        item {
            AppBreakdownCard(apps = uiState.monitoredApps)
        }

        item {
            WeeklyTrendCard()
        }

        item {
            AchievementsCard(
                problemsSolved = uiState.problemsSolved,
                breaksTaken = uiState.breaksTaken,
                timeSaved = uiState.timeSaved
            )
        }
    }
}

@Composable
fun StatisticsHeader() {
    Column {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Your digital wellness journey insights",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Spacing.ExtraSmall)
        )
    }
}

@Composable
fun TodayOverviewCard(
    totalScreenTime: Long, problemsSolved: Int, breaksTaken: Int, timeSaved: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XXXLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .gradientBackground(
                    AppGradients.primaryGradient(), alpha = 0.1f
                )
                .padding(Spacing.ExtraLarge)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSize.Medium)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.Large))

                // Statistics Grid
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)
                ) {
                    item {
                        StatCard(
                            title = "Screen Time",
                            value = "${totalScreenTime}m",
                            icon = Icons.Default.PhoneAndroid,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    item {
                        StatCard(
                            title = "Problems Solved",
                            value = problemsSolved.toString(),
                            icon = Icons.Default.Psychology,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    item {
                        StatCard(
                            title = "Breaks Taken",
                            value = breaksTaken.toString(),
                            icon = Icons.Default.SelfImprovement,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    item {
                        StatCard(
                            title = "Time Saved",
                            value = "${timeSaved}m",
                            icon = Icons.Default.Timer,
                            color = SuccessColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(CornerRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(IconSize.Medium)
            )
            Spacer(modifier = Modifier.height(Spacing.Small))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppBreakdownCard(apps: List<com.mindguard.app.data.model.AppUsage>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            if (apps.isEmpty()) {
                Text(
                    text = "No apps monitored yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                apps.forEach { app ->
                    AppUsageRow(app = app)
                    if (app != apps.last()) {
                        Spacer(modifier = Modifier.height(Spacing.Small))
                    }
                }
            }
        }
    }
}

@Composable
fun AppUsageRow(app: com.mindguard.app.data.model.AppUsage) {
    val progress = (app.currentUsage.toFloat() / app.dailyTimeLimit.toFloat()).coerceIn(0f, 1f)
    val isOverLimit = progress >= 1f

    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        // App info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${app.currentUsage}m / ${app.dailyTimeLimit}m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Progress
        Column(
            modifier = Modifier.width(80.dp), horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = if (isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyTrendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = SuccessColor
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            // Simple weekly chart representation
            val weeklyData = listOf(180, 150, 200, 120, 160, 140, 110) // Last 7 days in minutes
            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            Column {
                Text(
                    text = "Screen Time This Week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.Small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyData.forEachIndexed { index, minutes ->
                        WeeklyBar(
                            day = daysOfWeek[index],
                            minutes = minutes,
                            maxMinutes = weeklyData.maxOrNull() ?: 200,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.Small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Avg: ${weeklyData.average().toInt()}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Best: ${weeklyData.minOrNull()}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessColor
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyBar(
    day: String, minutes: Int, maxMinutes: Int, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar
        val progress = (minutes.toFloat() / maxMinutes.toFloat()).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(60.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(CornerRadius.Small)
                )
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height((60 * progress).dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        MaterialTheme.colorScheme.primary, RoundedCornerShape(CornerRadius.Small)
                    )
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

        // Day label
        Text(
            text = day,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Minutes
        Text(
            text = "${minutes}m",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AchievementsCard(
    problemsSolved: Int, breaksTaken: Int, timeSaved: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = WarningColor
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            // Achievement items - using real data
            AchievementItem(
                title = "Math Solver",
                description = if (problemsSolved == 0) "Solve your first problem!" else "Solved $problemsSolved problems",
                icon = Icons.Default.Psychology,
                progress = (problemsSolved / 10f).coerceIn(0f, 1f),
                isCompleted = problemsSolved >= 10
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            AchievementItem(
                title = "Break Taker",
                description = if (breaksTaken == 0) "Take your first mindful break!" else "Took $breaksTaken healthy breaks",
                icon = Icons.Default.SelfImprovement,
                progress = (breaksTaken / 5f).coerceIn(0f, 1f),
                isCompleted = breaksTaken >= 5
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            AchievementItem(
                title = "Time Saver",
                description = if (timeSaved == 0L) "Start saving time today!" else "Saved ${timeSaved} minutes",
                icon = Icons.Default.Timer,
                progress = (timeSaved / 60f).coerceIn(0f, 1f),
                isCompleted = timeSaved >= 60
            )
        }
    }
}

@Composable
fun AchievementItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    progress: Float,
    isCompleted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
            contentDescription = null,
            tint = if (isCompleted) SuccessColor else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.Small)
        )

        Spacer(modifier = Modifier.width(Spacing.Medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (isCompleted) SuccessColor else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}