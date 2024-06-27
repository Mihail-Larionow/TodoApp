package com.michel.feature.screens.todolistscreen.utils

sealed class ListScreenEffect {
    data class ShowSnackBarEffect(val message: String): ListScreenEffect()
}