package com.michel.core.ui.custom

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme


@Composable
fun CustomSnackBarHost(
    snackbarHostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = snackbarHostState,
    ) {
        CustomSnackBar(
            message = it.visuals.message,
            actionText = it.visuals.actionLabel,
            onClick = { it.performAction() }
        )
    }
}

@Composable
private fun CustomSnackBar(
    message: String,
    actionText: String?,
    onClick: () -> Unit
) {
    Snackbar(
        shape = TodoAppTheme.shape.container,
        containerColor = TodoAppTheme.color.elevated,
        action = {
            if (actionText != null) ActionButton(
                text = actionText,
                onClick = onClick
            )
        },
        content = {
            Text(
                text = message,
                style = TodoAppTheme.typography.body,
                color = TodoAppTheme.color.primary,
            )
        },
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = TodoAppTheme.typography.button,
            color = TodoAppTheme.color.blue
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun CustomSnackBarPreview() {
    TodoAppTheme {
        CustomSnackBar(
            message = stringResource(id = R.string.app_name),
            actionText = stringResource(id = R.string.retryUpperCase),
        ) {}
    }
}