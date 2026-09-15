package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SakinaLightColorScheme = lightColorScheme(
    primary = SakinaPrimary,
    onPrimary = SakinaOnPrimary,
    primaryContainer = SakinaPrimaryContainer,
    onPrimaryContainer = SakinaOnPrimaryContainer,
    secondary = SakinaSecondary,
    onSecondary = Color.White,
    secondaryContainer = SakinaSecondaryContainer,
    onSecondaryContainer = SakinaOnSecondaryContainer,
    tertiary = SakinaTertiary,
    onTertiary = Color.White,
    tertiaryContainer = SakinaTertiaryContainer,
    onTertiaryContainer = SakinaOnTertiaryContainer,
    background = SakinaBackground,
    onBackground = SakinaOnSurface,
    surface = SakinaSurface,
    onSurface = SakinaOnSurface,
    surfaceVariant = SakinaSurfaceContainerHighest,
    onSurfaceVariant = SakinaOnSurfaceVariant,
    surfaceTint = SakinaSurfaceTint,
    outline = SakinaOutline,
    outlineVariant = SakinaOutlineVariant,
    error = SakinaError,
    errorContainer = SakinaErrorContainer
)

private val SakinaDarkColorScheme = darkColorScheme(
    primary = SakinaPrimaryFixedDim,
    onPrimary = SakinaOnPrimaryFixed,
    primaryContainer = SakinaPrimaryContainer,
    onPrimaryContainer = SakinaPrimaryFixed,
    secondary = SakinaSecondaryFixedDim,
    onSecondary = SakinaPrimary,
    secondaryContainer = SakinaPrimaryContainer,
    onSecondaryContainer = SakinaSecondaryFixed,
    tertiary = SakinaTertiaryFixedDim,
    onTertiary = SakinaTertiary,
    tertiaryContainer = SakinaTertiaryContainer,
    onTertiaryContainer = SakinaTertiaryFixed,
    background = Color(0xFF0C1914),
    onBackground = Color(0xFFE0F0E8),
    surface = Color(0xFF0F1E19),
    onSurface = Color(0xFFE0F0E8),
    surfaceVariant = Color(0xFF1E332B),
    onSurfaceVariant = Color(0xFFB5C9C0),
    surfaceTint = SakinaPrimaryFixedDim,
    outline = SakinaOutline,
    outlineVariant = SakinaOutlineVariant
)

@Composable
fun SakinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SakinaDarkColorScheme else SakinaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards-compat alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = SakinaTheme(darkTheme = darkTheme, content = content)
