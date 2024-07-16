package com.michel.settings.utils


import com.michel.core.ui.viewmodel.ScreenEffect

/**
 * Side effect types of settings screen.
 */
sealed interface SettingsScreenEffect : ScreenEffect {
    data class ShowSnackBarEffect(val message: String) : SettingsScreenEffect
    data object LeaveScreenEffect : SettingsScreenEffect
}