package com.michel.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.todoapp.todoitemscreen.TodoItemScreen
import com.michel.todoapp.todolistscreen.TodoListScreen

@Composable
fun TodoAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.TodoListScreen.route
    ) {
        composable(route = Screen.TodoListScreen.route) {
            TodoListScreen(
                navigate = {
                    navController.navigate(Screen.TodoItemScreen.route)
                },
                onItemClick = { id ->
                    navController.navigate(Screen.TodoItemScreen.withArgs(id))
                }
            )
        }
        composable(
            route = Screen.TodoItemScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                }
            )
        ) { entry ->
            TodoItemScreen(id = entry.arguments?.getString("id"))
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