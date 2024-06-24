package com.michel.feature.screens.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.feature.screens.todoitemscreen.TodoItemScreen
import com.michel.feature.screens.todolistscreen.TodoListScreen

@Composable
fun TodoAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.TodoListScreen.route
    ) {
        composable(
            route = Screen.TodoListScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -500 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -500 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 500 },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 500
                    )
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { 500 },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 500
                    )
                )
            }
        ) {
            TodoListScreen(
                navigate = { id ->
                    navController.navigate(Screen.TodoItemScreen.withArgs(id))
                },
            )
        }
        composable(
            route = Screen.TodoItemScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                }
            ),
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 500 },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 500
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 500 },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 500
                    )
                )
            },
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 500 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) {
            TodoItemScreen(
                navigate = {
                    navController.navigate(Screen.TodoListScreen.route) {
                        popUpTo(Screen.TodoListScreen.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object TodoListScreen: Screen("todolist_screen")
    data object TodoItemScreen: Screen("todoitem_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}