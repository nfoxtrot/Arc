package com.nfoskette.arc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.nfoskette.arc.ui.AppShell
import com.nfoskette.arc.ui.theme.ARCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()'s default statusBarStyle/navigationBarStyle is
        // SystemBarStyle.auto(...), which picks icon color from the *device's*
        // system dark-mode setting (Resources.Configuration), not from any app
        // state. ARC's dark mode is a manual in-app toggle, deliberately
        // independent of system dark mode (see Theme.kt/docs/DESIGN.md) - so
        // enableEdgeToEdge() alone leaves status/nav bar icons stuck on
        // whatever the system theme was at launch. Toggle ARC to dark while the
        // device itself is in light mode and the status bar icons stay dark on
        // a now-dark background - invisible. Found during the 2026-08-26 audit.
        // Fixed below by driving WindowInsetsControllerCompat directly off our
        // own isDarkTheme state instead.
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val systemDark = isSystemInDarkTheme()
            // Default to the system setting once, then let the manual toggle take over.
            remember { isDarkTheme = systemDark }

            val window = this.window
            SideEffect {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.isAppearanceLightStatusBars = !isDarkTheme
                controller.isAppearanceLightNavigationBars = !isDarkTheme
            }

            ARCTheme(darkTheme = isDarkTheme) {
                AppShell(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}
