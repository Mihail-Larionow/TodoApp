package com.michel.core.data.repository

import com.michel.core.data.models.ApplicationTheme

/**
 * Interface that provides settings storage logic.
 */
interface SettingsRepository {

    fun saveTheme(theme: ApplicationTheme)

    fun getTheme(): ApplicationTheme

}