package com.michel.common.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val SHARED_PREFS_NAME = "todoapp_configuration"
private const val KEY_THEME_NAME = "todoapp_theme"

/**
 * Contains information about the application settings.
 */
@Singleton
class SettingsData @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Mutable and immutable theme state flow.
     */
    private val _theme = MutableStateFlow(false)
    val theme = _theme.asStateFlow()

    /**
     * Sets theme of the application.
     *
     * @param themeName - field that contains theme's name.
     */
    fun setTheme(themeName: String) {
        saveSetting(name = KEY_THEME_NAME, value = themeName)
    }

    /**
     * Returns immutable theme flow.
     */
    fun getTheme() = getSetting(name = KEY_THEME_NAME)

    /**
     * Returns immutable theme flow.
     */
    fun getThemeFlow() = getSettingFlow(name = KEY_THEME_NAME)


    /**
     * Saves setting configuration.
     *
     * @param name - field that contains setting's name.
     * @param value - field that contains setting's value.
     */
    private fun saveSetting(name: String, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }

    /**
     * Returns setting configuration.
     *
     * @param name - field that contains setting's name.
     */
    private fun getSetting(name: String): String? {
        return sharedPreferences.getString(name, null)
    }

    /**
     * Returns setting configuration as flow.
     *
     * @param name - field that contains setting's name.
     */
    private fun getSettingFlow(name: String) = callbackFlow<String?> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == name) trySend(sharedPreferences.getString(key, null))
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        if (sharedPreferences.contains(name)) {
            send(sharedPreferences.getString(name, null))
        }
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.buffer(Channel.UNLIMITED)

}