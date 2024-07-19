package com.michel.core.data.repository

import com.michel.common.utils.SettingsData
import com.michel.core.data.models.ApplicationTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements settings storage logic.
 */
internal class SettingsRepositoryImpl @Inject constructor(
    private val settingsData: SettingsData,
) : SettingsRepository {

    /**
     * Saves theme configuration in shared preferences.
     */
    override fun saveTheme(theme: ApplicationTheme) {
        settingsData.setTheme(theme.name)
    }

    /**
     * Returns theme configuration as flow.
     */
    override fun getThemeFlow(): Flow<ApplicationTheme> {
        return settingsData.getThemeFlow().map {
            when (it) {
                "Light" -> ApplicationTheme.Light
                "Dark" -> ApplicationTheme.Dark
                else -> ApplicationTheme.System
            }
        }
    }

    /**
     * Returns theme configuration.
     */
    override fun getTheme(): ApplicationTheme {
        return when (settingsData.getTheme()) {
            "Light" -> ApplicationTheme.Light
            "Dark" -> ApplicationTheme.Dark
            else -> ApplicationTheme.System
        }
    }

}