package com.nfoskette.arc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// NOTE on Material You dynamic color: minSdk 31 was chosen specifically so dynamic
// color is *available* on every installable device (see docs/DESIGN.md ยง5), but the
// ARC design system also specifies exact brand color tokens for both light and dark
// mode (ยง3). Those are two different things: dynamic color derives its palette from
// the user's wallpaper (varies per device/user), while ARC's tokens are a fixed
// brand identity. This theme uses the fixed ARC tokens, NOT dynamicLightColorScheme/
// dynamicDarkColorScheme, so the brand looks consistent everywhere. If per-device
// dynamic theming is wanted later instead, that's a real design decision to make
// explicitly, not something to fall back to silently.

private val ArcLightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightCard,
    primaryContainer = LightAccentSoft,
    onPrimaryContainer = LightInk,
    // secondaryContainer drives FilledTonalButton's default color. Left unset, it
    // falls back to Material3's auto-derived tone (a generic lavender-purple that
    // has nothing to do with ARC's pink) - caught this by actually looking at a
    // rendered tonal button (2026-08-26), not by reading the token list. Mirroring
    // primaryContainer here keeps every tonal button visually on-brand.
    secondaryContainer = LightAccentSoft,
    onSecondaryContainer = LightInk,
    background = LightBg,
    onBackground = LightInk,
    surface = LightCard,
    onSurface = LightInk,
    surfaceVariant = LightBgAlt,
    onSurfaceVariant = LightInkSoft,
    outline = LightLine,
    outlineVariant = LightLine,
)

private val ArcDarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkBg,
    primaryContainer = DarkAccentSoft,
    onPrimaryContainer = DarkInk,
    secondaryContainer = DarkAccentSoft,
    onSecondaryContainer = DarkInk,
    background = DarkBg,
    onBackground = DarkInk,
    surface = DarkCard,
    onSurface = DarkInk,
    surfaceVariant = DarkBgAlt,
    onSurfaceVariant = DarkInkSoft,
    outline = DarkLine,
    outlineVariant = DarkLine,
)

@Composable
fun ARCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ArcDarkColorScheme else ArcLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
