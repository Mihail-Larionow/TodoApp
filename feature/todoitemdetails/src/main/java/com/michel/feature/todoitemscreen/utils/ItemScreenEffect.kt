package com.michel.feature.todoitemscreen.utils

import com.michel.core.ui.viewmodel.ScreenEffect

/**
 * Side effect types of item screen
 */
internal sealed interface ItemScreenEffect : ScreenEffect {
    data class ShowSnackBarEffect(val message: String) : ItemScreenEffect
    data object LeaveScreenEffect : ItemScreenEffect
}