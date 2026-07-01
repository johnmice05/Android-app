package com.example.androidapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Books : Screen("books")
    object Chapters : Screen("chapters/{bookName}/{language}")
    object Verses : Screen("verses/{bookName}/{chapter}/{language}")
    
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/")
                append(arg)
            }
        }
    }
}
