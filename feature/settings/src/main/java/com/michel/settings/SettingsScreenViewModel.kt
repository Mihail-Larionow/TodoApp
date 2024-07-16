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

@HiltViewModel
internal class SettingsScreenViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : ViewModelBase<SettingsScreenState, SettingsScreenIntent, SettingsScreenEffect>(SettingsScreenState()) {

    init{
        val theme = repository.getTheme()
        changeApplicationTheme(theme)
    }

    /**
     * Handles intents.
     *
     * @param intent - the intent need to be handled.
     */
    override fun handleIntent(intent: ScreenIntent) {
        when(intent) {
            is SettingsScreenIntent.ChangeThemeIntent -> changeApplicationTheme(intent.theme)
            SettingsScreenIntent.LeaveScreenIntent -> leaveScreen()
        }
    }

    /**
     * Updates application theme.
     *
     * @param theme - theme needed to save.
     */
    private fun changeApplicationTheme(theme: ApplicationTheme) {
        setState { copy(applicationTheme = theme) }
        repository.saveTheme(theme)
    }

    /**
     * Starts side effect that navigates to the items list screen.
     */
    private fun leaveScreen() {
        setEffect { SettingsScreenEffect.LeaveScreenEffect }
    }

}