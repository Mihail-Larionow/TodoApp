package com.michel.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.feature.authscreen.AuthScreen
import com.michel.feature.todoitemscreen.TodoItemScreen
import com.michel.feature.todolistscreen.TodoListScreen
import com.michel.settings.SettingsScreen

/**
 * Contains logic for navigating between screens
 */
@Composable
internal fun TodoAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destination.AuthScreen.route
    ) {
        composable(
            route = Destination.AuthScreen.route,
        ) {
            AuthScreen(navigate = { navigateToTodoList(navController) })
        }

        composable(
            route = Destination.SettingsScreen.route,
        ) {
            SettingsScreen(navigate = { navigateToTodoList(navController) })
        }

        composable(route = Destination.TodoListScreen.route) {
            TodoListScreen(
                navigateToItem = { navigateToTodoItem(navController, it) },
                navigateToSettings = { navigateToSettings(navController) }
            )
        }

        composable(
            route = Destination.TodoItemScreen.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            TodoItemScreen(navigate = { navigateToTodoList(navController) })
        }
    }
}

private fun navigateToTodoList(navController: NavHostController) {
    navController.navigate(Destination.TodoListScreen.route) {
        popUpTo(Destination.TodoListScreen.route) { inclusive = true }
    }
}

private fun navigateToSettings(navController: NavHostController) {
    navController.navigate(Destination.SettingsScreen.route)
}

private fun navigateToTodoItem(navController: NavHostController, id: String) {
    navController.navigate(Destination.TodoItemScreen.withArgs(id))
}


