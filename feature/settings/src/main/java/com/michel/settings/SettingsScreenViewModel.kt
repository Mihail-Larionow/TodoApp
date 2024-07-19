package com.michel.settings

import com.michel.core.data.models.ApplicationTheme
import com.michel.core.data.repository.SettingsRepository
import com.michel.core.ui.viewmodel.ScreenIntent
import com.michel.core.ui.viewmodel.ViewModelBase
import com.michel.settings.utils.SettingsScreenEffect
import com.michel.settings.utils.SettingsScreenIntent
import com.michel.settings.utils.SettingsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel of the settings screen.
 */
@HiltViewModel
internal class SettingsScreenViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : ViewModelBase<SettingsScreenState, SettingsScreenIntent, SettingsScreenEffect>(
    SettingsScreenState()
) {

    init {
        val theme = repository.getTheme()
        setApplicationTheme(theme)
    }

    /**
     * Handles intents.
     *
     * @param intent - the intent need to be handled.
     */
    override fun handleIntent(intent: ScreenIntent) {
        when (intent) {
            is SettingsScreenIntent.ChangeThemeIntent -> setApplicationTheme(intent.theme)
            SettingsScreenIntent.LeaveToListScreenIntent -> leaveToListScreen()
            SettingsScreenIntent.LeaveToAboutScreenIntent -> leaveToAboutScreen()
        }
    }

    /**
     * Sets application theme.
     *
     * @param newTheme - theme needed to set.
     */
    private fun setApplicationTheme(newTheme: ApplicationTheme) {
        setState { copy(applicationTheme = newTheme) }
        repository.saveTheme(newTheme)
    }

    /**
     * Starts side effect that navigates to the items list screen.
     */
    private fun leaveToListScreen() {
        setEffect { SettingsScreenEffect.LeaveToListScreenEffect }
    }

    /**
     * Starts side effect that navigates to the about app screen.
     */
    private fun leaveToAboutScreen() {
        setEffect { SettingsScreenEffect.LeaveToAboutScreenEffect }
    }

}