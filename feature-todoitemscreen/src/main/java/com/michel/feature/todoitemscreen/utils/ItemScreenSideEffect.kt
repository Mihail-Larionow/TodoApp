package com.michel.feature.todoitemscreen.utils

sealed interface ItemScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ItemScreenSideEffect
    data object LeaveScreenSideEffect : ItemScreenSideEffect
}