package com.michel.core.data.repository

import com.michel.core.data.models.ApplicationTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface that provides settings storage logic.
 */
interface SettingsRepository {

    fun saveTheme(theme: ApplicationTheme)

    fun getThemeFlow(): Flow<ApplicationTheme>

    fun getTheme(): ApplicationTheme

}