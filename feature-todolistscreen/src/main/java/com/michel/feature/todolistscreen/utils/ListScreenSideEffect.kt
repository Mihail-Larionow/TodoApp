package com.michel.feature.todolistscreen.utils

sealed interface ListScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : ListScreenSideEffect
}