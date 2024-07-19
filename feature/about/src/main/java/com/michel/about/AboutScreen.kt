package com.michel.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.michel.core.ui.custom.CustomSnackBarHost

@Composable
fun AboutScreen(
    snackBarHostState: SnackbarHostState,
    navigateToSettings: () -> Unit
) {
    Scaffold(
        snackbarHost = { CustomSnackBarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Content(
            modifier = Modifier.padding(innerPadding),
            onEvent = navigateToSettings
        )

    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onEvent: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                val aboutDiv = AboutDiv(
                    context = context,
                    onEvent = onEvent,
                )
                aboutDiv.getView()
            }
        )
    }
}