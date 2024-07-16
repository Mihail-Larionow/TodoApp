package com.michel.todoapp.navigation

/**
 * Contains navigation destination routes
 */
sealed class Destination(val route: String) {
    data object AuthScreen : Destination("auth")
    data object SettingsScreen : Destination("settings")
    data object TodoListScreen : Destination("todolist")
    data object TodoItemScreen : Destination("todoitem")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg -> append("/$arg") }
        }
    }
}