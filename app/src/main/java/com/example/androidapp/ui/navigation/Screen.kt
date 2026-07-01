package com.example.androidapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    
    // Add more screens here as needed
}
