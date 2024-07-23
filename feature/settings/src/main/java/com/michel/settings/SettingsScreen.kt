package com.michel.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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
    snackBarHostState: SnackbarHostState,
    navigateToListScreen: () -> Unit,
    navigateToAboutScreen: () -> Unit,
) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectSideEffects(
            viewModel = viewModel,
            snackBarHostState = snackBarHostState,
            navigateToListScreen = navigateToListScreen,
            navigateToAboutScreen = navigateToAboutScreen,
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
            .background(TodoAppTheme.color.backPrimary)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Body(
            screenState = screenState,
            onEvent = onEvent,
            modifier = modifier.padding(top = TodoAppTheme.size.toolBar),
        )

        IconButton(
            onClick = { onEvent(SettingsScreenIntent.LeaveToListScreenIntent) },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_exit),
                contentDescription = stringResource(com.michel.core.ui.R.string.exit),
                tint = TodoAppTheme.color.primary,
                modifier = Modifier.size(TodoAppTheme.size.standardIcon)
            )
        }

        IconButton(
            onClick = { onEvent(SettingsScreenIntent.LeaveToAboutScreenIntent) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_info),
                contentDescription = stringResource(com.michel.core.ui.R.string.about_app),
                tint = TodoAppTheme.color.secondary,
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

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = com.michel.core.ui.R.string.themeOfTheApplicationText),
            style = TodoAppTheme.typography.title,
            color = TodoAppTheme.color.primary,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        ThemeRadioButtons(
            options = options,
            selectedTheme = screenState.applicationTheme,
            onEvent = onEvent,
            modifier = Modifier.align(Alignment.CenterVertically)
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
        modifier = modifier.padding(start = 8.dp, end = 4.dp)
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
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            TodoAppTheme.color.white
        } else {
            TodoAppTheme.color.tertiary
        },
        label = "Content animated color"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            TodoAppTheme.color.blue
        } else {
            TodoAppTheme.color.disable
        },
        label = "Container animated color"
    )

    val painterResource = when (theme) {
        ApplicationTheme.Dark -> painterResource(id = com.michel.core.ui.R.drawable.ic_moon)
        ApplicationTheme.Light -> painterResource(id = com.michel.core.ui.R.drawable.ic_sun)
        ApplicationTheme.System -> painterResource(id = com.michel.core.ui.R.drawable.ic_phone)
    }

    val contentDescription = when (theme) {
        ApplicationTheme.Dark -> stringResource(com.michel.core.ui.R.string.dark_theme)
        ApplicationTheme.Light -> stringResource(com.michel.core.ui.R.string.light_theme)
        ApplicationTheme.System -> stringResource(com.michel.core.ui.R.string.system_theme)
    }

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = containerColor,
                shape = TodoAppTheme.shape.container
            )
            .background(
                color = containerColor.copy(alpha = 0.5f),
                shape = TodoAppTheme.shape.container
            )
    ) {
        Icon(
            painter = painterResource,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier
                .padding(8.dp)
                .size(TodoAppTheme.size.standardIcon)
                .semantics {
                    role = Role.RadioButton
                    selected = isSelected
                }
        )
    }
}

/**
 * Handles side effects.
 */
private suspend fun collectSideEffects(
    viewModel: SettingsScreenViewModel,
    snackBarHostState: SnackbarHostState,
    navigateToListScreen: () -> Unit,
    navigateToAboutScreen: () -> Unit,
) {
    viewModel.effect.collect { effect ->
        when (effect) {
            SettingsScreenEffect.LeaveToListScreenEffect -> navigateToListScreen()
            SettingsScreenEffect.LeaveToAboutScreenEffect -> navigateToAboutScreen()
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