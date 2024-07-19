package com.michel.todoapp.utils

/**
 * State of the main activity.
 */
data class TodoAppActivityState(
    val isDarkTheme: Boolean = false,
    val isSystemTheme: Boolean = true,
)