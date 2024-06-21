package com.michel.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.michel.todoapp.todoitemscreen.TodoItemScreen
import com.michel.todoapp.todolistscreen.TodoListScreen
import com.michel.todoapp.todolistscreen.TodoListScreensThree

@Composable
fun TodoAppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.TodoListScreen.route
    ) {
        composable(route = Screen.TodoListScreen.route) {
            TodoListScreen(
                onItemClick = { id ->
                    navController.navigate(Screen.TodoItemScreen.withArgs(id))
                },
                modifier = modifier
            )
        }
        composable(
            route = Screen.TodoItemScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                }
            )
        ) {
            TodoItemScreen(
                navigate = {
                    navController.navigate(Screen.TodoListScreen.route)
                },
                modifier = modifier
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