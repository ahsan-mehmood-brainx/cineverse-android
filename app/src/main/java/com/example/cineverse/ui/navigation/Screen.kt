package com.example.cineverse.ui.navigation

import androidx.annotation.IdRes
import com.example.cineverse.R

/**
 * Type-safe wrapper over the destination IDs declared in res/navigation/nav_graph.xml,
 * used wherever code needs to reason about a destination without hardcoding the raw ID.
 */
sealed class Screen(@IdRes val destinationId: Int) {
    data object Home : Screen(R.id.homeFragment)
    data object Categories : Screen(R.id.categoriesFragment)
    data object Search : Screen(R.id.searchFragment)
    data object Favorites : Screen(R.id.favoritesFragment)
    data object Profile : Screen(R.id.profileFragment)
    data object MovieDetail : Screen(R.id.movieDetailFragment)
}
