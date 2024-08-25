package com.michel.core.ui.custom

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme
import kotlinx.coroutines.delay


@Composable
fun CustomSnackBarHost(
    snackBarHostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = snackBarHostState,
    ) {
        CustomSnackBar(
            message = it.visuals.message,
            actionText = it.visuals.actionLabel,
            withDismissAction = it.visuals.withDismissAction,
            onClick = { it.performAction() }
        )
    }
}

@Composable
private fun CustomSnackBar(
    message: String,
    actionText: String?,
    withDismissAction: Boolean,
    onClick: () -> Unit
) {
    Content(
        message = message,
        actionText = actionText,
        withDismissAction = withDismissAction,
        onClick = onClick
    )
}

@Composable
private fun Content(
    message: String,
    actionText: String?,
    withDismissAction: Boolean,
    onClick: () -> Unit,
) {
    var countDown by remember { mutableLongStateOf(5000) }

    if (withDismissAction) {

        LaunchedEffect(countDown) {
            if (countDown > 0) {
                delay(1000)
                countDown -= 1000
            }
        }
    }

    val background by animateColorAsState(
        targetValue = if (countDown > 3000L) {
            TodoAppTheme.color.elevated
        } else if (countDown > 1000L) {
            TodoAppTheme.color.elevated.copy(alpha = 0.8f)
        } else {
            TodoAppTheme.color.elevated.copy(alpha = 0.5f)
        }
    )

    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = background,
                shape = TodoAppTheme.shape.container
            )
            .padding(8.dp)
            .fillMaxWidth()

    ) {
        if (withDismissAction) {
            TimerText(
                countDown = countDown,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = message,
            style = TodoAppTheme.typography.body,
            color = TodoAppTheme.color.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
                .weight(1f)
        )

        if (actionText != null) ActionButton(
            text = actionText,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TimerText(
    modifier: Modifier = Modifier,
    countDown: Long,
) {
    val timerColor by animateColorAsState(
        targetValue = if (countDown > 3000L) {
            TodoAppTheme.color.primary
        } else {
            TodoAppTheme.color.red
        },
        label = ""
    )

    Box(modifier = modifier) {
        CircularProgressIndicator(
            progress = { 1 - countDown / 5000f },
            color = timerColor,
            modifier = Modifier
                .size(TodoAppTheme.size.standardIcon)
                .align(Alignment.Center)
        )

        Text(
            text = "${countDown / 1000}",
            style = TodoAppTheme.typography.body,
            color = timerColor,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp)
        )
    }
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
            withDismissAction = true,
        ) {}
    }
}