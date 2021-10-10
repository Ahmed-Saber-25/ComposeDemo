package com.example.composedemo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomScreen(val route: String, val title: String, val icon: ImageVector,val badgeCount:Int=0) {
    object Home: BottomScreen("home", "Home", Icons.Filled.Home,20)
    object Favourite: BottomScreen("favourite", "Fav", Icons.Filled.Favorite,15)
    object Search: BottomScreen("search", "Search", Icons.Filled.Search)
    object Setting: BottomScreen("setting", "Setting", Icons.Filled.Settings)
    object User: BottomScreen("user", "User", Icons.Filled.Person)
}

val bottomNavigationItems = listOf(
    BottomScreen.Home,
    BottomScreen.Favourite,
    BottomScreen.Search,
    BottomScreen.Setting,
    BottomScreen.User
)
