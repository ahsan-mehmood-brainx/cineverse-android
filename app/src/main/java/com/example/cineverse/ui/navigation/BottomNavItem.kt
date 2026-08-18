package com.example.cineverse.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BottomNavItem(
    val screen: Screen,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int
)
