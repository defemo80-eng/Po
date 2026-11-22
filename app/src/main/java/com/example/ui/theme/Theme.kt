package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DeFittDarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DeFittBlack,
    primaryContainer = NeonGreenContainer,
    onPrimaryContainer = NeonGreenBright,
    secondary = DeFittGold,
    onSecondary = DeFittBlack,
    secondaryContainer = DeFittGoldContainer,
    onSecondaryContainer = DeFittGold,
    tertiary = NeonGreenBright,
    onTertiary = DeFittBlack,
    background = DeFittBlack,
    onBackground = DeFittWhite,
    surface = DeFittSurface,
    onSurface = DeFittWhite,
    surfaceVariant = DeFittSurfaceCard,
    onSurfaceVariant = DeFittTextSecondary,
    surfaceContainerHigh = DeFittSurfaceElevated,
    outline = DeFittBorder,
    error = DeFittError,
    onError = DeFittWhite
)

private val DeFittShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun DeFittTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DeFittDarkColorScheme,
        typography = DeFittTypography,
        shapes = DeFittShapes,
        content = content
    )
}
