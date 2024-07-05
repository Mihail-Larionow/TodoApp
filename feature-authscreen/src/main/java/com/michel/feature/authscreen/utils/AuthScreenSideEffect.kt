package com.michel.feature.authscreen.utils

/**
 * Side effect types of auth screen
 */
sealed interface AuthScreenSideEffect {
    data class ShowSnackBarSideEffect(val message: String) : AuthScreenSideEffect
    data object StartAuthSideEffect : AuthScreenSideEffect
    data object LeaveScreenSideEffect : AuthScreenSideEffect
}