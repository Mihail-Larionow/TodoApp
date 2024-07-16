package com.michel.core.data.repository

import android.content.Context
import com.michel.core.data.datasource.remote.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val SHARED_PREFS_NAME = "yandex_prefs"
private const val KEY_YANDEX_TOKEN_NAME = "token"

/**
 * Implements token storage logic
 */
internal class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : TokenRepository {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    // Сохраняет токен в shared preferences
    override fun setToken(token: String) {
        sharedPreferences.edit().putString(KEY_YANDEX_TOKEN_NAME, token).apply()
    }

    // Достает токен из shared preferences
    override fun getToken(): String? {
        val token = sharedPreferences.getString(KEY_YANDEX_TOKEN_NAME, null)
        return token
    }

}