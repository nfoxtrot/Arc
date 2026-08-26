package com.nfoskette.arc.ui

// The three bottom-tab destinations from docs/DESIGN.md ยง4 (Home / Plan / Lessons).
// Signup is deliberately NOT a route here — it's a one-time bottom-sheet modal,
// not a permanent screen (see SignupSheet.kt).
sealed class ArcRoute(val route: String, val label: String) {
    data object Home : ArcRoute("home", "Home")
    data object Builder : ArcRoute("builder", "Plan")
    data object Lesson : ArcRoute("lesson", "Lessons")

    companion object {
        val bottomTabs = listOf(Home, Builder, Lesson)
    }
}
