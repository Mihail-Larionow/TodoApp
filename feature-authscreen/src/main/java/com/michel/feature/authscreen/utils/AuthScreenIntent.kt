package com.michel.feature.authscreen.utils

import com.michel.core.ui.viewmodel.ScreenIntent

/**
 * Intent types of auth screen
 */
internal sealed interface AuthScreenIntent : ScreenIntent {
    data object StartAuthIntent : AuthScreenIntent
    data object SaveGradleTokenIntent : AuthScreenIntent
    data object FailAuthIntent : AuthScreenIntent
    data object CancelAuthIntent : AuthScreenIntent
    data class SaveTokenIntent(val token: String) : AuthScreenIntent
}