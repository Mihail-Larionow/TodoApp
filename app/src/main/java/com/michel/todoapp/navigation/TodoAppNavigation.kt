package com.michel.todoapp.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.about.AboutScreen
import com.michel.core.ui.animations.Transition
import com.michel.core.ui.custom.CustomSnackBarHost
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
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarHost = @Composable { CustomSnackBarHost(snackBarHostState) }

    NavHost(
        navController = navController,
        startDestination = Destination.AuthScreen.route
    ) {
        composable(
            route = Destination.AuthScreen.route,
            enterTransition = Transition.screenEnterTop,
            exitTransition = Transition.screenExitTop,
        ) {
            AuthScreen(
                snackBarHostState = snackBarHostState,
                navigate = { navigateToTodoList(navController) }
            )
        }

        composable(
            route = Destination.AboutScreen.route,
            enterTransition = Transition.screenEnterTop,
            exitTransition = Transition.screenExitTop,
        ) {
            AboutScreen(
                snackBarHostState = snackBarHostState,
                navigateToSettings = { navigateToSettings(navController) }
            )
        }

        composable(
            route = Destination.SettingsScreen.route,
            enterTransition = Transition.screenEnterBottom,
            exitTransition = Transition.screenExitBottom,
        ) {
            SettingsScreen(
                snackBarHostState = snackBarHostState,
                navigateToListScreen = { navigateToTodoList(navController) },
                navigateToAboutScreen = { navigateToAbout(navController) }
            )
        }

        composable(
            route = Destination.TodoListScreen.route,
            enterTransition = Transition.screenEnterLeft,
            exitTransition = Transition.screenExitLeft,
        ) {
            TodoListScreen(
                snackBarHost = snackBarHost,
                snackBarHostState = snackBarHostState,
                navigateToItem = { navigateToTodoItem(navController, it) },
                navigateToSettings = { navigateToSettings(navController) }
            )
        }

        composable(
            route = Destination.TodoItemScreen.route + "/{id}",
            enterTransition = Transition.screenEnterRight,
            exitTransition = Transition.screenExitRight,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            TodoItemScreen(
                snackBarHost = snackBarHost,
                snackBarHostState = snackBarHostState,
                navigate = { navigateToTodoList(navController) }
            )
        }
    }
}

private fun navigateToTodoList(navController: NavHostController) {
    navController.navigate(Destination.TodoListScreen.route) {
        popUpTo(Destination.TodoListScreen.route) { inclusive = true }
    }
}

private fun navigateToAbout(navController: NavHostController) {
    navController.navigate(Destination.AboutScreen.route)
}

private fun navigateToSettings(navController: NavHostController) {
    navController.navigate(Destination.SettingsScreen.route) {
        popUpTo(Destination.SettingsScreen.route) { inclusive = true }
    }
}

private fun navigateToTodoItem(navController: NavHostController, id: String) {
    navController.navigate(Destination.TodoItemScreen.withArgs(id))
}


