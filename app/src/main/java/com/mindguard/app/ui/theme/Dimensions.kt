package com.mindguard.app.ui.theme

import androidx.compose.ui.unit.dp

// Modern Design System - Spacing and Dimensions
object Spacing {
    // Base spacing units following 8dp grid system
    val None = 0.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val XXLarge = 48.dp
    val XXXLarge = 64.dp

    // Specific spacing for common use cases
    val CardPadding = Medium
    val ScreenPadding = Large
    val SectionSpacing = ExtraLarge
    val ItemSpacing = Small
    val ButtonSpacing = Medium
    val ListItemSpacing = Medium
}

object CornerRadius {
    // Consistent corner radius system
    val None = 0.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 20.dp
    val XXLarge = 24.dp
    val XXXLarge = 28.dp
    val Circle = 50.dp

    // Specific radius for components
    val Button = Medium
    val Card = Large
    val Dialog = ExtraLarge
    val BottomSheet = XXLarge
}

object Elevation {
    // Material 3 elevation system
    val Level0 = 0.dp
    val Level1 = 1.dp
    val Level2 = 3.dp
    val Level3 = 6.dp
    val Level4 = 8.dp
    val Level5 = 12.dp
}

object IconSize {
    // Consistent icon sizing
    val Small = 16.dp
    val Medium = 24.dp
    val Large = 32.dp
    val ExtraLarge = 48.dp
    val XXLarge = 64.dp
    val Huge = 96.dp
}

object BorderWidth {
    // Border and divider thickness
    val Thin = 1.dp
    val Medium = 2.dp
    val Thick = 4.dp
}

object ComponentHeight {
    // Standard component heights
    val Button = 48.dp
    val TextField = 56.dp
    val ListItem = 64.dp
    val AppBar = 64.dp
    val BottomNav = 80.dp
    val FAB = 56.dp
    val Chip = 32.dp
}