package com.michel.feature.screens.todoitemscreen.utils

sealed class ItemScreenEffect {
    data class ShowSnackBarEffect(val message: String): ItemScreenEffect()
    data object LeaveScreenEffect: ItemScreenEffect()
}