package com.michel.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.data.models.ApplicationTheme
import com.michel.core.ui.custom.CustomSnackBarHost
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.settings.utils.SettingsScreenEffect
import com.michel.settings.utils.SettingsScreenIntent
import com.michel.settings.utils.SettingsScreenState

/**
 * Contains UI implementation of settings screen.
 */
@Composable
fun SettingsScreen(
    navigate: () -> Unit,
) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        collectSideEffects(
            viewModel = viewModel,
            snackBarHostState = snackBarHostState,
            navigate = navigate
        )
    }

    Scaffold(
        snackbarHost = { CustomSnackBarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Content(
            screenState = screenState,
            onEvent = viewModel::handleIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    screenState: SettingsScreenState,
    onEvent: (SettingsScreenIntent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(TodoAppTheme.color.backSecondary)
    ) {
        Body(
            screenState = screenState,
            onEvent = onEvent,
            modifier = modifier,
        )

        IconButton(
            onClick = { onEvent(SettingsScreenIntent.LeaveScreenIntent) },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_exit),
                contentDescription = stringResource(com.michel.core.ui.R.string.cancelUpperCase),
                tint = TodoAppTheme.color.primary,
                modifier = Modifier.size(TodoAppTheme.size.standardIcon)
            )
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    screenState: SettingsScreenState,
    onEvent: (SettingsScreenIntent) -> Unit
) {
    val options = listOf(
        ApplicationTheme.Light,
        ApplicationTheme.Dark,
        ApplicationTheme.System
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = com.michel.core.ui.R.string.themeOfTheApplicationText),
            style = TodoAppTheme.typography.title,
            color = TodoAppTheme.color.primary
        )
        Spacer(
            modifier = Modifier.height(4.dp)
        )
        ThemeRadioButtons(
            options = options,
            selectedTheme = screenState.applicationTheme,
            onEvent = onEvent,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun ThemeRadioButtons(
    modifier: Modifier = Modifier,
    options: List<ApplicationTheme>,
    selectedTheme: ApplicationTheme,
    onEvent: (SettingsScreenIntent) -> Unit,
) {
    Row(
        modifier = modifier.padding(8.dp)
    ) {
        options.forEach {
            ThemeButton(
                theme = it,
                isSelected = it == selectedTheme,
                onClick = { onEvent(SettingsScreenIntent.ChangeThemeIntent(it)) }
            )
        }
    }
}

@Composable
private fun ThemeButton(
    theme: ApplicationTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            TodoAppTheme.color.white
        } else {
            TodoAppTheme.color.tertiary
        },
        label = ""
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            TodoAppTheme.color.blue
        } else {
            TodoAppTheme.color.disable
        },
        label = ""
    )

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = containerColor,
                shape = TodoAppTheme.shape.container
            )
            .background(
                color = containerColor.copy(alpha = 0.5f),
                shape = TodoAppTheme.shape.container
            )
    ) {
        Text(
            text = theme.name,
            style = TodoAppTheme.typography.body,
            color = textColor,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Handles side effects.
 */
private suspend fun collectSideEffects(
    viewModel: SettingsScreenViewModel,
    snackBarHostState: SnackbarHostState,
    navigate: () -> Unit
) {
    viewModel.effect.collect { effect ->
        when (effect) {
            SettingsScreenEffect.LeaveScreenEffect -> navigate()
            is SettingsScreenEffect.ShowSnackBarEffect -> snackBarHostState.showSnackbar(effect.message)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    val screenState = SettingsScreenState()
    Content(screenState = screenState) { }
}