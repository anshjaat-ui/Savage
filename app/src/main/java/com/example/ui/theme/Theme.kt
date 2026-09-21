package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FireOrangePrimary,
    onPrimary = WarmWhiteText,
    primaryContainer = CharcoalSurfaceVariant,
    onPrimaryContainer = AmberGlow,
    secondary = SaffronSecondary,
    onSecondary = CharcoalBackground,
    secondaryContainer = CharcoalSurfaceVariant,
    onSecondaryContainer = AmberGlow,
    tertiary = CrimsonBurnTertiary,
    onTertiary = WarmWhiteText,
    background = CharcoalBackground,
    onBackground = WarmWhiteText,
    surface = CharcoalSurface,
    onSurface = WarmWhiteText,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = WarmMutedText,
    outline = CharcoalBorder
)

private val LightColorScheme = darkColorScheme(
    // Defaulting to the rich, contrasty Bezzati Dark theme as primary signature look
    primary = FireOrangePrimary,
    onPrimary = WarmWhiteText,
    primaryContainer = CharcoalSurfaceVariant,
    onPrimaryContainer = AmberGlow,
    secondary = SaffronSecondary,
    onSecondary = CharcoalBackground,
    tertiary = CrimsonBurnTertiary,
    background = CharcoalBackground,
    surface = CharcoalSurface,
    surfaceVariant = CharcoalSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to edgy, fiery dark mode
    dynamicColor: Boolean = false, // Keep consistent fire/desi styling
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CharcoalBackground.toArgb()
                window.navigationBarColor = CharcoalBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
