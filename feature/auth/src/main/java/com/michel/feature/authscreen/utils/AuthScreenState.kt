package com.michel.feature.authscreen.utils

import com.michel.core.ui.viewmodel.ScreenState

/**
 * Contains state of auth screen
 */
data class AuthScreenState(
    val hasGradleToken: Boolean = true
) : ScreenState