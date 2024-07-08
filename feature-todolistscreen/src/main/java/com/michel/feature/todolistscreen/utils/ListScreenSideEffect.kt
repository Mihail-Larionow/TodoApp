package com.michel.feature.todolistscreen.utils

/**
 * Side effects types of items list screen
 */
internal sealed interface ListScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ListScreenSideEffect
    data class LeaveScreenSideEffect(val id: String) : ListScreenSideEffect
}