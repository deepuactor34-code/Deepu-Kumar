package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color(0xFF0D0221),
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = NeonPurple,
    secondary = NeonCyan,
    onSecondary = Color(0xFF002229),
    secondaryContainer = Color(0xFF0A2B36),
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF381023),
    onTertiaryContainer = NeonPink,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF373357),
    outlineVariant = Color(0xFF262242),
    error = CyberError,
    onError = Color.Black
)

private val LightColorScheme = darkColorScheme(
    // Yesmin AI shines best in a futuristic dark theme
    primary = NeonPurpleDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADBFF),
    onPrimaryContainer = Color(0xFF25005A),
    secondary = NeonCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F4F7),
    onSecondaryContainer = Color(0xFF00363D),
    tertiary = NeonPinkSubtle,
    onTertiary = Color.White,
    background = Color(0xFFF7F6FC),
    onBackground = Color(0xFF14131C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14131C),
    surfaceVariant = Color(0xFFECEBF5),
    onSurfaceVariant = Color(0xFF49455B),
    outline = Color(0xFF7A7591)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Default to the tailored futuristic cyber theme for immersion
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
