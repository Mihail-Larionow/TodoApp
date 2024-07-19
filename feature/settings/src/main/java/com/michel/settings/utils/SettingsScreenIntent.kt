package com.michel.settings.utils

import com.michel.core.data.models.ApplicationTheme
import com.michel.core.ui.viewmodel.ScreenIntent


/**
 * Intent types of settings screen.
 */
internal sealed interface SettingsScreenIntent : ScreenIntent {
    data class ChangeThemeIntent(val theme: ApplicationTheme) : SettingsScreenIntent
    data object LeaveToListScreenIntent : SettingsScreenIntent
    data object LeaveToAboutScreenIntent : SettingsScreenIntent
}