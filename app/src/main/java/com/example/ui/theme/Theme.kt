package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AviatorRed,
    onPrimary = Color.White,
    primaryContainer = AviatorRedDark,
    onPrimaryContainer = Color.White,
    secondary = CyanNeon,
    onSecondary = Color.Black,
    secondaryContainer = CyanNeonDark,
    onSecondaryContainer = Color.White,
    tertiary = JetXGold,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun CrashPredictorTheme(
    content: @Composable () -> Unit
) {
    // We intentionally maintain the atmospheric immersive dark cockpit theme across all devices
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
