package com.michel.feature.todoitemscreen.utils

/**
 * Side effect types of item screen
 */
internal sealed interface ItemScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ItemScreenSideEffect
    data object LeaveScreenSideEffect : ItemScreenSideEffect
}