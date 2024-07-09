package com.michel.feature.todolistscreen.utils

import com.michel.core.ui.viewmodel.ScreenEffect

/**
 * Side effects types of items list screen
 */
internal sealed interface ListScreenEffect : ScreenEffect {
    data class ShowSnackBarEffect(val message: String) : ListScreenEffect
    data class LeaveScreenEffect(val id: String) : ListScreenEffect
}