package com.michel.settings.utils

import com.michel.core.data.models.ApplicationTheme
import com.michel.core.ui.viewmodel.ScreenState


/**
 * Contains state of settings screen.
 */
data class SettingsScreenState(
    val applicationTheme: ApplicationTheme = ApplicationTheme.System,
) : ScreenState