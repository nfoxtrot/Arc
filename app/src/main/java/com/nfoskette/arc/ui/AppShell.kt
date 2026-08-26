package com.nfoskette.arc.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nfoskette.arc.ui.screens.BuilderScreen
import com.nfoskette.arc.ui.screens.HomeScreen
import com.nfoskette.arc.ui.screens.LessonScreen
import com.nfoskette.arc.ui.screens.SignupSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShell(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val routeState = remember { RouteState() }
    // First-launch signup bottom sheet (docs/DESIGN.md ยง4): shown once while signed
    // out. There's no real auth/persistence yet, so this just defaults to shown —
    // wiring "signed out" + "already seen it" state is real backend/DataStore work
    // still to do, not something to fake silently as if it were solved.
    var showSignupSheet by remember { mutableStateOf(true) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val canGoBack = navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (canGoBack) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                title = {
                    Text(
                        text = "ARC",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                ArcRoute.bottomTabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(tabIcon(tab)) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ArcRoute.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
            }
        ) {
            composable(ArcRoute.Home.route) {
                HomeScreen(
                    onPlanRoute = { navController.navigate(ArcRoute.Builder.route) },
                    onSeeSample = { navController.navigate(ArcRoute.Lesson.route) }
                )
            }
            composable(ArcRoute.Builder.route) {
                BuilderScreen(
                    routeState = routeState,
                    onConfirmed = { navController.navigate(ArcRoute.Lesson.route) }
                )
            }
            composable(ArcRoute.Lesson.route) {
                LessonScreen(routeState = routeState)
            }
        }
    }

    if (showSignupSheet) {
        SignupSheet(onDismiss = { showSignupSheet = false })
    }
}

// Waypoint-style single-glyph icons standing in for the ⌂ ✎ ▤ icon set from the
// wireframe spec, until real vector icons are added.
private fun tabIcon(tab: ArcRoute): String = when (tab) {
    ArcRoute.Home -> "⌂"
    ArcRoute.Builder -> "✎"
    ArcRoute.Lesson -> "▤"
}
