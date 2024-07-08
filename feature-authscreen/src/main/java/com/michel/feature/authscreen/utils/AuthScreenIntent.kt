package com.michel.feature.authscreen.utils

/**
 * Intent types of auth screen
 */
internal sealed interface AuthScreenIntent {
    data object StartAuthIntent : AuthScreenIntent
    data object SaveGradleTokenIntent : AuthScreenIntent
    data object FailAuthIntent : AuthScreenIntent
    data object CancelAuthIntent : AuthScreenIntent
    data class SaveTokenIntent(val token: String): AuthScreenIntent
}