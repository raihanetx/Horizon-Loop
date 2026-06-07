package com.horizonloop.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    secondary = Mid,
    background = Deep,
    surface = Surface,
    onPrimary = Deep,
    onSecondary = Dark,
    onBackground = Dark,
    onSurface = Dark,
    surfaceVariant = Muted,
    onSurfaceVariant = Mid
)

@Composable
fun HorizonLoopTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Deep.toArgb()
            window.navigationBarColor = Deep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}