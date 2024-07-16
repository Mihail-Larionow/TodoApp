package com.michel.core.data.repository

import android.content.Context
import com.michel.core.data.models.ApplicationTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val SHARED_PREFS_NAME = "todoapp_configuration"
private const val KEY_THEME_NAME = "todoapp_theme"

/**
 * Implements settings storage logic.
 */
internal class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
) : SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Saves theme configuration in shared preferences.
     */
    override fun saveTheme(theme: ApplicationTheme) {
        sharedPreferences.edit().putString(KEY_THEME_NAME, theme.name).apply()
    }

    /**
     * Gets theme configuration from shared preferences.
     */
    override fun getTheme(): ApplicationTheme {
        val themeName = sharedPreferences.getString(KEY_THEME_NAME, null)
        return when(themeName) {
            "Light" -> ApplicationTheme.Light
            "Dark" -> ApplicationTheme.Dark
            else -> ApplicationTheme.System
        }
    }

}