package com.michel.feature.screens.todolistscreen.utils

sealed class ListScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ListScreenSideEffect()
}