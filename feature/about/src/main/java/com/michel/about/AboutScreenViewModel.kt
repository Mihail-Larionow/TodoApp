package com.michel.about

import androidx.lifecycle.ViewModel
import com.michel.about.utils.AboutScreenState
import com.michel.core.data.models.ApplicationTheme
import com.michel.core.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class AboutScreenViewModel @Inject constructor(
    repository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AboutScreenState())
    val state = _state.asStateFlow()

    init {
        val theme = repository.getTheme()
        setTheme(theme)
    }

    /**
     * Sets screen theme.
     *
     * @param theme - theme needed to set.
     */
    private fun setTheme(theme: ApplicationTheme) {
        when (theme) {
            ApplicationTheme.Dark -> _state.update {
                it.copy(
                    isDarkTheme = true,
                    isSystemTheme = false
                )
            }

            ApplicationTheme.Light -> _state.update {
                it.copy(
                    isDarkTheme = false,
                    isSystemTheme = false
                )
            }

            ApplicationTheme.System -> _state.update { it.copy(isSystemTheme = true) }
        }
    }
}