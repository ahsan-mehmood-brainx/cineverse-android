package com.example.cineverse.ui.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

/** Top-level destinations shown in both the bottom navigation bar and the drawer. */
val topLevelDestinations = setOf(
    Screen.Home.destinationId,
    Screen.Categories.destinationId,
    Screen.Search.destinationId,
    Screen.Favorites.destinationId,
    Screen.Profile.destinationId
)

/**
 * Wires a single [NavController] to the drawer, the toolbar and the bottom navigation bar so
 * they all stay in sync, following the Navigation Component's single-Activity pattern.
 */
fun AppCompatActivity.setupAppNavigation(
    navController: NavController,
    drawerLayout: DrawerLayout,
    navigationView: NavigationView,
    bottomNavigationView: BottomNavigationView
): AppBarConfiguration {
    val appBarConfiguration = AppBarConfiguration(topLevelDestinations, drawerLayout)
    navigationView.setupWithNavController(navController)
    bottomNavigationView.setupWithNavController(navController)
    return appBarConfiguration
}
