package com.michel.feature.authscreen

import android.content.res.Configuration
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.authscreen.utils.AuthScreenIntent
import com.michel.feature.authscreen.utils.AuthScreenSideEffect
import com.michel.feature.authscreen.utils.AuthScreenState
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

/**
 * Contains UI implementation of auth screen
 */
@Composable
fun AuthScreen(
    navigate: () -> Unit
) {
    val viewModel: AuthScreenViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val sdk = YandexAuthSdk.create(YandexAuthOptions(LocalContext.current))
    val authLauncher = rememberLauncherForActivityResult(contract = sdk.contract) { result ->
        handleResult(
            result = result,
            onEvent = { viewModel.onEvent(it) }
        )
    }

    LaunchedEffect(Unit) {
        collectSideEffects(
            viewModel = viewModel,
            authLauncher = authLauncher,
            snackBarHostState = snackBarHostState,
            navigate = navigate
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val padding = innerPadding
        Content(
            screenState = screenState,
            onEvent = { viewModel.onEvent(it) }
        )
    }
}

@Composable
private fun Content(
    screenState: AuthScreenState,
    onEvent: (AuthScreenIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TodoAppTheme.color.backPrimary)
    ) {
        Body(
            modifier = Modifier.align(Alignment.Center),
            screenState = screenState,
            onEvent = onEvent
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    screenState: AuthScreenState,
    onEvent: (AuthScreenIntent) -> Unit
) {
    Column(modifier = modifier) {
        AuthButton(
            text = stringResource(com.michel.core.ui.R.string.with_yandex_passport),
            onClick = { onEvent(AuthScreenIntent.StartAuthIntent) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(com.michel.core.ui.R.string.or),
            color = TodoAppTheme.color.tertiary,
            style = TodoAppTheme.typography.button,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        AuthButton(
            text = stringResource(com.michel.core.ui.R.string.with_gradle_token),
            enabled = screenState.hasGradleToken,
            onClick = { onEvent(AuthScreenIntent.SaveGradleTokenIntent) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun AuthButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            contentColor = TodoAppTheme.color.primary,
            containerColor = TodoAppTheme.color.backSecondary,
            disabledContentColor = TodoAppTheme.color.tertiary,
            disabledContainerColor = TodoAppTheme.color.disable
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = TodoAppTheme.typography.button
        )
    }
}

// Собирает сайд эффекты
private suspend fun collectSideEffects(
    viewModel: AuthScreenViewModel,
    snackBarHostState: SnackbarHostState,
    authLauncher: ManagedActivityResultLauncher<YandexAuthLoginOptions, YandexAuthResult>,
    navigate: () -> Unit
) {
    val loginOptions = YandexAuthLoginOptions()
    viewModel.effect.collect { effect ->
        when (effect) {
            is AuthScreenSideEffect.ShowSnackBarSideEffect -> snackBarHostState.showSnackbar(effect.message)
            AuthScreenSideEffect.StartAuthSideEffect -> authLauncher.launch(loginOptions)
            AuthScreenSideEffect.LeaveScreenSideEffect -> navigate()
        }
    }
}

// Обрабатывет результат авторизации
private fun handleResult(
    result: YandexAuthResult,
    onEvent: (AuthScreenIntent) -> Unit
) {
    when (result) {
        is YandexAuthResult.Success -> onEvent(AuthScreenIntent.SaveTokenIntent(result.token.value))
        YandexAuthResult.Cancelled -> onEvent(AuthScreenIntent.CancelAuthIntent)
        is YandexAuthResult.Failure -> onEvent(AuthScreenIntent.FailAuthIntent)
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthScreenPreview() {
    val screenState = AuthScreenState(hasGradleToken = false)
    TodoAppTheme {
        Content(
            screenState = screenState,
            onEvent = { }
        )
    }
}