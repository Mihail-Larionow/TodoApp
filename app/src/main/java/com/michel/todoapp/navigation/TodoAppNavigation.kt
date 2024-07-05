package com.michel.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.feature.authscreen.AuthScreen

/**
 * Contains logic for navigating between screens
 */
@Composable
fun TodoAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destination.AuthScreen.route
    ) {
        composable(
            route = Destination.AuthScreen.route,
        ) {
            AuthScreen(
                navigate = {
                    navController.navigate(Destination.TodoListScreen.route) {
                        popUpTo(Destination.TodoListScreen.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(route = Destination.TodoListScreen.route) {
            com.michel.feature.todolistscreen.TodoListScreen(
                navigate = { id ->
                    navController.navigate(Destination.TodoItemScreen.withArgs(id))
                },
            )
        }

        composable(
            route = Destination.TodoItemScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            com.michel.feature.todoitemscreen.TodoItemScreen(
                navigate = {
                    navController.navigate(Destination.TodoListScreen.route) {
                        popUpTo(Destination.TodoListScreen.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}



