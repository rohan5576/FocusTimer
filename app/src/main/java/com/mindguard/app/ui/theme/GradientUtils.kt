package com.mindguard.app.ui.theme

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Modern gradient definitions for the app
object AppGradients {
    @Composable
    fun primaryGradient(): Brush {
        val isDark = isSystemInDarkTheme()
        return if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    GradientStartDark, GradientEndDark
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    GradientStart, GradientEnd
                )
            )
        }
    }

    @Composable
    fun successGradient(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFF4CAF50), Color(0xFF81C784)
            )
        )
    }

    @Composable
    fun warningGradient(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF9800), Color(0xFFFFB74D)
            )
        )
    }

    @Composable
    fun errorGradient(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                Color(0xFFE53935), Color(0xFFEF5350)
            )
        )
    }

    @Composable
    fun shimmerGradient(): Brush {
        val isDark = isSystemInDarkTheme()
        val colors = if (isDark) {
            listOf(
                Color(0xFF2A2A2A), Color(0xFF3A3A3A), Color(0xFF2A2A2A)
            )
        } else {
            listOf(
                Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0)
            )
        }

        return Brush.linearGradient(
            colors = colors, start = Offset.Zero, end = Offset(1000f, 1000f)
        )
    }

    @Composable
    fun cardGradient(): Brush {
        val isDark = isSystemInDarkTheme()
        return if (isDark) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A1A1A), Color(0xFF0F0F0F)
                ), radius = 1000f
            )
        } else {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF), Color(0xFFF8F8F8)
                ), radius = 1000f
            )
        }
    }
}

// Animation utilities
object AppAnimations {
    // Standard durations
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
    const val EXTRA_SLOW = 1000

    // Easing curves
    val FastOutSlowIn = FastOutSlowInEasing
    val LinearOut = LinearOutSlowInEasing
    val FastOut = FastOutLinearInEasing
    val Linear = LinearEasing

    // Common animation specs
    val fadeInSpec = tween<Float>(
        durationMillis = NORMAL, easing = FastOutSlowIn
    )

    val fadeOutSpec = tween<Float>(
        durationMillis = FAST, easing = FastOutSlowIn
    )

    val slideInSpec = tween<Float>(
        durationMillis = NORMAL, easing = FastOutSlowIn
    )

    val scaleInSpec = tween<Float>(
        durationMillis = NORMAL, easing = FastOutSlowIn
    )

    val bounceSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
    )
}

// Modifier extensions for gradients and animations
fun Modifier.gradientBackground(
    gradient: Brush, alpha: Float = 1f
) = this.drawBehind {
    drawRect(
        brush = gradient, alpha = alpha
    )
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var offsetX by remember { mutableStateOf(-1000f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    LaunchedEffect(Unit) {
        offsetX = 1000f
    }

    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent, Color.White.copy(alpha = 0.3f), Color.Transparent
            ),
            start = Offset(animatedOffsetX, 0f),
            end = Offset(animatedOffsetX + 200f, size.height)
        )
        drawRect(brush = brush)
    }
}

fun Modifier.pulseEffect(
    color: Color = Color.White.copy(alpha = 0.1f), duration: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse_alpha"
    )

    this.drawBehind {
        drawRect(color = color.copy(alpha = alpha))
    }
}

fun Modifier.glowEffect(
    color: Color, borderRadius: Float = 0f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "glow_alpha"
    )

    this.drawBehind {
        drawRoundRect(
            color = color.copy(alpha = glowAlpha),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(borderRadius)
        )
    }
}