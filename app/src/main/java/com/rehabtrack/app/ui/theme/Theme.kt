package com.rehabtrack.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Background,
    secondary = PrimaryDark,
    onSecondary = Background,
    tertiary = PrimaryLight,
    onTertiary = Text,
    background = Background,
    onBackground = Text,
    surface = Background,
    onSurface = Text,
    surfaceVariant = InfoBackground,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Error,
    onError = Background
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Background,
    secondary = PrimaryDark,
    onSecondary = Background,
    tertiary = PrimaryLight,
    onTertiary = Text,
    background = Background,
    onBackground = Text,
    surface = Background,
    onSurface = Text,
    surfaceVariant = InfoBackground,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Error,
    onError = Background
)

@Composable
fun RehabTrackTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}