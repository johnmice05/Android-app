package com.example.androidapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidapp.ui.screens.books.BooksScreen
import com.example.androidapp.ui.screens.chapters.ChaptersScreen
import com.example.androidapp.ui.screens.home.HomeScreen
import com.example.androidapp.ui.screens.verses.VersesScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onExploreClick = { navController.navigate(Screen.Books.route) }
            )
        }

        composable(Screen.Books.route) {
            BooksScreen(
                onBookSelected = { book ->
                    navController.navigate(
                        "chapters/${book.name}/${book.language}"
                    )
                }
            )
        }

        composable("chapters/{bookName}/{language}") { backStackEntry ->
            val bookName = backStackEntry.arguments?.getString("bookName") ?: ""
            val language = backStackEntry.arguments?.getString("language") ?: "English"

            ChaptersScreen(
                bookName = bookName,
                bookLanguage = language,
                onChapterSelected = { chapter ->
                    navController.navigate(
                        "verses/$bookName/${chapter.chapterNumber}/$language"
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("verses/{bookName}/{chapter}/{language}") { backStackEntry ->
            val bookName = backStackEntry.arguments?.getString("bookName") ?: ""
            val chapter = backStackEntry.arguments?.getString("chapter")?.toIntOrNull() ?: 1
            val language = backStackEntry.arguments?.getString("language") ?: "English"

            VersesScreen(
                bookName = bookName,
                chapter = chapter,
                language = language,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
