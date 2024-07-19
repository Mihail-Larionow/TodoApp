package com.michel.about

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.about.utils.AboutDiv
import com.michel.core.ui.custom.CustomSnackBarHost
import com.michel.core.ui.theme.TodoAppTheme

@Composable
fun AboutScreen(
    snackBarHostState: SnackbarHostState,
    navigateToSettings: () -> Unit
) {
    val viewModel: AboutScreenViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val isDarkTheme = if (screenState.isSystemTheme) {
        isSystemInDarkTheme()
    } else {
        screenState.isDarkTheme
    }

    Scaffold(
        snackbarHost = { CustomSnackBarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Content(
            modifier = Modifier.padding(innerPadding),
            darkTheme = isDarkTheme,
            onEvent = navigateToSettings
        )

    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    onEvent: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TodoAppTheme.color.backPrimary)
    ) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                AboutDiv.getView(
                    context = context,
                    darkTheme = darkTheme,
                    lifecycleOwner = lifecycleOwner,
                    onEvent = onEvent,
                )
            }
        )
    }
}