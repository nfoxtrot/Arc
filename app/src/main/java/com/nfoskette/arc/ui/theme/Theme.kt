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
// dynamicDarkColorScheme, so the brand looks consistent everywhere. Revisited on
// 2026-08-26 (options researched and discussed first - see docs/DESIGN.md ยง9) and
// reconfirmed: keep the fixed tokens exactly as-is. What changed instead is filling
// in the surfaceContainer tonal tiers below, which were previously left undefined
// and silently fell back to Material3's generic neutral-derived tones - the same
// class of bug the secondaryContainer fix (also ยง9) caught for tonal buttons, just
// for the surfaceContainer roles Material You's "layered container" look depends on.

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
    // surfaceContainer tiers (2026-08-26): Material You's "objects and containers"
    // look - Cards, sheets, nav bars sitting as distinct layered surfaces rather than
    // flat/outlined boxes - relies on these 5 tones existing as a real light-to-dark
    // ramp. Left unset, all 5 fall back to Material3's generic neutral-gray default,
    // which reads as "unthemed" next to ARC's warm palette. No new colors invented:
    // stepped between the two tokens the design system already has for this range
    // (LightBg, the app background, and LightCard, true white) rather than guessing
    // new hex values - lowest/low lean toward background, high/highest toward card.
    surfaceContainerLowest = LightBg,
    surfaceContainerLow = LightBgAlt,
    surfaceContainer = LightBgAlt,
    surfaceContainerHigh = LightCard,
    surfaceContainerHighest = LightCard,
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
    surfaceContainerLowest = DarkBg,
    surfaceContainerLow = DarkBgAlt,
    surfaceContainer = DarkBgAlt,
    surfaceContainerHigh = DarkCard,
    surfaceContainerHighest = DarkCard,
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
