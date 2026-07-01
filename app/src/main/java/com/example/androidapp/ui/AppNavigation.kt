package com.example.androidapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.ui.navigation.AppNavigation

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}
