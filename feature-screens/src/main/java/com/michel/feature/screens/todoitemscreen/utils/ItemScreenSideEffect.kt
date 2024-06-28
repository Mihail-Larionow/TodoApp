package com.michel.feature.screens.todoitemscreen.utils

sealed class ItemScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ItemScreenSideEffect()
    data object LeaveScreenSideEffect : ItemScreenSideEffect()
}