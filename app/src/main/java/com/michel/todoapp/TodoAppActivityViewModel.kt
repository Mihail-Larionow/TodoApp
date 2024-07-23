package com.michel.todoapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michel.core.data.models.ApplicationTheme
import com.michel.core.data.repository.SettingsRepository
import com.michel.todoapp.utils.TodoAppActivityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * ViewModel of the main activity.
 */
@HiltViewModel
class TodoAppActivityViewModel @Inject constructor(
    repository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TodoAppActivityState())
    val state = _state.asStateFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.i("ui", "${throwable.message}")
    }

    private val currentState
        get() = state.value

    init {
        scope.launch {
            repository.getThemeFlow().collect {
                setApplicationTheme(it)
            }
        }
    }

    /**
     * Sets application theme.
     *
     * @param newTheme - theme needed to set.
     */
    private fun setApplicationTheme(newTheme: ApplicationTheme) {
        when (newTheme) {
            ApplicationTheme.Dark -> setState { copy(isDarkTheme = true, isSystemTheme = false) }
            ApplicationTheme.Light -> setState { copy(isDarkTheme = false, isSystemTheme = false) }
            ApplicationTheme.System -> setState { copy(isSystemTheme = true) }
        }
    }

    /**
     * Sets new screen state.
     */
    private fun setState(state: TodoAppActivityState.() -> TodoAppActivityState) {
        val newState = currentState.state()
        _state.update {
            newState
        }
    }
}