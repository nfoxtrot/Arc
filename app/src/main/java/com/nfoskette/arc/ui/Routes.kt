package com.nfoskette.arc.ui

// Bottom-tab destinations. The original wireframe spec (docs/DESIGN.md ยง4) only
// specified three (Home / Plan / Lessons); Profile was added later as a 4th tab —
// a deliberate change to that spec, decided explicitly rather than assumed (see
// ยง9 changelog).
sealed class ArcRoute(val route: String, val label: String) {
    data object Home : ArcRoute("home", "Home")
    data object Builder : ArcRoute("builder", "Plan")
    data object Lesson : ArcRoute("lesson", "Lessons")
    data object Profile : ArcRoute("profile", "Profile")

    companion object {
        val bottomTabs = listOf(Home, Builder, Lesson, Profile)
    }
}
