package com.michel.feature.authscreen.utils

import com.michel.core.ui.viewmodel.ScreenEffect

/**
 * Side effect types of auth screen
 */
sealed interface AuthScreenEffect : ScreenEffect {
    data class ShowSnackBarEffect(val message: String) : AuthScreenEffect
    data object StartAuthEffect : AuthScreenEffect
    data object LeaveScreenEffect : AuthScreenEffect
}