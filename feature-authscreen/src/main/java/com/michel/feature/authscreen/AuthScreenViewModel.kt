package com.michel.feature.authscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.repository.TokenRepository
import com.michel.feature.authscreen.utils.AuthScreenIntent
import com.michel.feature.authscreen.utils.AuthScreenSideEffect
import com.michel.feature.authscreen.utils.AuthScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel for auth screen
 */
@HiltViewModel
internal class AuthScreenViewModel @Inject constructor(
    @Named("TOKEN") private val gradleToken: String,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(AuthScreenState(hasGradleToken = gradleToken != "{YOUR TOKEN}"))
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<AuthScreenSideEffect> = MutableSharedFlow()
    val effect: SharedFlow<AuthScreenSideEffect> = _effect.asSharedFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    // Обрабатывает приходящие интенты
    fun onEvent(intent: AuthScreenIntent) {
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
        scope.launch(Dispatchers.IO) {
            _effect.emit(AuthScreenSideEffect.StartAuthSideEffect)
        }
    }

    // Сохраняет токен из gradle.properties
    private fun saveGradleToken() {
        saveToken(gradleToken)
    }

    // Сохраняет токен на устройстве
    private fun saveToken(token: String) {
        scope.launch(Dispatchers.IO) {
            tokenRepository.saveToken(token)
            _effect.emit(AuthScreenSideEffect.LeaveScreenSideEffect)
        }
    }

    // Запускает сайд эффект, чтобы вывести снекбар об отмененной авторизации
    private fun onAuthCancelled() {
        scope.launch(Dispatchers.IO) {
            _effect.emit(AuthScreenSideEffect.ShowSnackBarSideEffect("Отмена авторизации"))
        }
    }

    // Запускает сайд эффект, чтобы вывести снекбар о неудачной авторизации
    private fun onAuthFailed() {
        scope.launch(Dispatchers.IO) {
            _effect.emit(AuthScreenSideEffect.ShowSnackBarSideEffect("Ошибка во время авторизации"))
        }
    }

}