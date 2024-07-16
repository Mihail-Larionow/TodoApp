package com.michel.feature.authscreen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.michel.core.data.repository.TokenRepository
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.feature.authscreen.utils.AuthScreenEffect
import com.michel.feature.authscreen.utils.AuthScreenIntent
import com.michel.feature.authscreen.utils.AuthScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel for auth screen
 */
@HiltViewModel
internal class AuthScreenViewModel @Inject constructor(
    private val repository: TokenRepository,
    @Named("TOKEN_OAUTH") private val gradleToken: String,
) : ViewModelBase<AuthScreenState, AuthScreenIntent, AuthScreenEffect>(AuthScreenState()) {

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    // Обрабатывает приходящие интенты
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            AuthScreenIntent.StartAuthIntent -> startAuth()
            AuthScreenIntent.SaveGradleTokenIntent -> saveGradleToken()
            is AuthScreenIntent.SaveTokenIntent -> saveToken(intent.token)
            AuthScreenIntent.FailAuthIntent -> onAuthFailed()
            AuthScreenIntent.CancelAuthIntent -> onAuthCancelled()
        }
    }

    // Начинает авторизацию
    private fun startAuth() {
        setEffect { AuthScreenEffect.StartAuthEffect }
    }

    // Сохраняет токен из gradle.properties
    private fun saveGradleToken() {
        saveToken(gradleToken)
    }

    // Сохраняет токен на устройстве
    private fun saveToken(token: String) {
        repository.setToken(token)
        setEffect { AuthScreenEffect.LeaveScreenEffect }
    }

    // Запускает сайд эффект, чтобы вывести снекбар об отмененной авторизации
    private fun onAuthCancelled() {
        setEffect { AuthScreenEffect.ShowSnackBarEffect("Отмена авторизации") }
    }

    // Запускает сайд эффект, чтобы вывести снекбар о неудачной авторизации
    private fun onAuthFailed() {
        setEffect { AuthScreenEffect.ShowSnackBarEffect("Ошибка во время авторизации") }
    }

}