package com.michel.feature.todoitemscreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.data.models.Importance
import com.michel.core.ui.custom.CustomDatePicker
import com.michel.core.ui.custom.CustomTextField
import com.michel.core.ui.custom.TodoDivider
import com.michel.core.ui.extensions.bottomShadow
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.todoitemscreen.utils.ItemScreenIntent
import com.michel.feature.todoitemscreen.utils.ItemScreenSideEffect
import com.michel.feature.todoitemscreen.utils.TodoItemScreenState
import java.util.Date

private val TOP_BAR_HEIGHT = 56.dp

@Composable
fun TodoItemScreen(navigate: () -> Unit) {

    val viewModel: TodoItemViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ItemScreenSideEffect.ShowSnackBarSideEffect -> snackBarHostState.showSnackbar(
                    effect.message
                )

                ItemScreenSideEffect.LeaveScreenSideEffect -> navigate()
            }
        }
    }

    Content(
        screenState = screenState,
        snackBarHostState = snackBarHostState
    ) { event ->
        when (event) {
            ItemScreenIntent.ToListScreenIntent -> navigate()
            else -> {
                viewModel.onEvent(event)
            }
        }
    }
}

@Composable
private fun Content(
    screenState: TodoItemScreenState,
    snackBarHostState: SnackbarHostState,
    onEvent: (ItemScreenIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    var deadlineDateText by remember { mutableStateOf(screenState.deadline.toDateText()) }

    val showShadow: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset > 0 ||
                    listState.firstVisibleItemIndex > 0
        }
    }

    val headerShadow = if (showShadow) 4.dp else 0.dp

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            Header(
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier
                    .height(TodoAppTheme.size.toolBar)
                    .bottomShadow(
                        shadow = headerShadow
                    )
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
                    .padding(
                        start = 16.dp,
                        end = 12.dp,
                        bottom = 4.dp,
                        top = 4.dp
                    )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = TodoAppTheme.color.backPrimary)
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            Body(
                listState = listState,
                screenState = screenState,
                deadlineDateText = deadlineDateText,
                onEvent = onEvent
            )

            AnimatedVisibility(
                visible = !screenState.loading && screenState.failed,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column {
                    Icon(
                        painter = painterResource(com.michel.core.ui.R.drawable.ic_no_signal),
                        contentDescription = "icon",
                        tint = TodoAppTheme.color.red,
                        modifier = Modifier
                            .size(TodoAppTheme.size.standardIcon)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = screenState.errorMessage,
                        color = TodoAppTheme.color.red,
                        style = TodoAppTheme.typography.body,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { onEvent(ItemScreenIntent.GetItemInfoIntent) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "ОБНОВИТЬ",
                            color = TodoAppTheme.color.red,
                            style = TodoAppTheme.typography.button
                        )
                    }
                }
            }
        }

        if (screenState.datePickerExpanded) TodoDatePicker(
            date = screenState.deadline,
            onEvent = { event ->
                when (event) {
                    is ItemScreenIntent.SetDeadlineDateIntent -> {
                        deadlineDateText = event.deadline.toDateText()
                    }

                    else -> {}
                }
                onEvent(event)
            }
        )
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    screenState: TodoItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TOP_BAR_HEIGHT)
            .padding(
                start = 8.dp,
                end = 8.dp
            )
    ) {
        Icon(
            painter = painterResource(com.michel.core.ui.R.drawable.ic_exit),
            contentDescription = stringResource(com.michel.core.ui.R.string.cancelUpperCase),
            tint = TodoAppTheme.color.primary,
            modifier = Modifier
                .size(TodoAppTheme.size.standardIcon)
                .clickable { onEvent(ItemScreenIntent.ToListScreenIntent) }
                .align(Alignment.CenterVertically)
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )

        val textColor = if (screenState.text == "" || screenState.loading) {
            TodoAppTheme.color.disable
        } else {
            TodoAppTheme.color.blue
        }

        TextButton(
            onClick = {
                if (screenState.text != "" && !screenState.loading) {
                    onEvent(ItemScreenIntent.SaveIntent)
                }
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(com.michel.core.ui.R.string.saveUpperCase),
                color = textColor,
                style = TodoAppTheme.typography.button
            )
        }
    }
}

@Composable
private fun Body(
    listState: LazyListState,
    screenState: TodoItemScreenState,
    deadlineDateText: String,
    onEvent: (ItemScreenIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = TodoAppTheme.color.backPrimary
            )
    ) {
        item {
            CustomTextField(
                text = screenState.text,
                shape = TodoAppTheme.shape.container,
                enabled = !screenState.loading,
                onValueChanged = { onEvent(ItemScreenIntent.SetTextIntent(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 16.dp
                    )
            )
        }
        item {
            PriorityField(
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier.padding(
                    all = 16.dp
                )
            )
            TodoDivider(
                modifier = Modifier.padding(
                    all = 16.dp
                )
            )
        }

        item {
            DeadlineField(
                date = deadlineDateText,
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier.padding(
                    all = 16.dp
                )
            )
            TodoDivider()
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (screenState.text != "" && !screenState.loading) {
                            onEvent(ItemScreenIntent.DeleteIntent)
                        }
                    }
                    .padding(
                        all = 16.dp
                    )
            ) {
                val buttonColor = if (screenState.text == "" || screenState.loading) {
                    TodoAppTheme.color.disable
                } else {
                    TodoAppTheme.color.red
                }

                Icon(
                    painter = painterResource(com.michel.core.ui.R.drawable.ic_delete),
                    tint = buttonColor,
                    contentDescription = stringResource(com.michel.core.ui.R.string.deleteContentDescription),
                    modifier = Modifier
                        .size(TodoAppTheme.size.standardIcon)
                        .align(Alignment.CenterVertically)
                )
                Spacer(
                    modifier = Modifier.width(16.dp)
                )
                Text(
                    text = stringResource(com.michel.core.ui.R.string.deleteUpperCase),
                    color = buttonColor,
                    style = TodoAppTheme.typography.button,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoDatePicker(
    modifier: Modifier = Modifier,
    date: Long,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val datePickerState = rememberDatePickerState(date)

    CustomDatePicker(
        datePickerState = datePickerState,
        onConfirm = {
            onEvent(ItemScreenIntent.SetDeadlineDateIntent(it))
            onEvent(ItemScreenIntent.SetDatePickerStateIntent(false))
        },
        onDismiss = {
            onEvent(ItemScreenIntent.SetDatePickerStateIntent(false))
        },
        modifier = modifier
    )
}

@Composable
private fun PriorityField(
    modifier: Modifier = Modifier,
    screenState: TodoItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {

    val options = listOf(
        Importance.Standard,
        Importance.Low,
        Importance.High
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !screenState.loading) {
                onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(true))
            }
    ) {
        Text(
            text = stringResource(com.michel.core.ui.R.string.priority),
            color = TodoAppTheme.color.primary,
            style = TodoAppTheme.typography.body
        )
        Spacer(
            modifier = Modifier.height(4.dp)
        )

        val textColor = if (screenState.importance == Importance.High) {
            TodoAppTheme.color.red
        } else {
            TodoAppTheme.color.tertiary
        }

        Text(
            text = screenState.importance.text,
            color = textColor,
            style = TodoAppTheme.typography.body
        )
        DropdownMenu(
            expanded = screenState.priorityMenuExpanded,
            onDismissRequest = { onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(false)) },
            modifier = Modifier.background(
                shape = RectangleShape,
                color = TodoAppTheme.color.elevated
            )
        ) {
            options.forEach {

                val optionTextColor = if (it == Importance.High) {
                    TodoAppTheme.color.red
                } else {
                    TodoAppTheme.color.primary
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.text,
                            color = optionTextColor,
                            style = TodoAppTheme.typography.body
                        )
                    },
                    onClick = {
                        onEvent(ItemScreenIntent.SetPriorityIntent(it))
                        onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(false))
                    }
                )
            }
        }
    }
}

@Composable
private fun DeadlineField(
    modifier: Modifier = Modifier,
    date: String,
    screenState: TodoItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Column {
            Text(
                text = stringResource(com.michel.core.ui.R.string.do_before),
                color = TodoAppTheme.color.primary,
                style = TodoAppTheme.typography.body
            )
            if (screenState.hasDeadline) {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Text(
                    text = date,
                    color = TodoAppTheme.color.blue,
                    style = TodoAppTheme.typography.subhead,
                    modifier = Modifier.clickable {
                        onEvent(ItemScreenIntent.SetDatePickerStateIntent(true))
                    }
                )
            }
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = screenState.hasDeadline,
            enabled = !screenState.loading,
            onCheckedChange = { onEvent(ItemScreenIntent.SetDeadlineStateIntent(it)) },
            colors = SwitchDefaults.colors(
                checkedIconColor = TodoAppTheme.color.blue,
                checkedThumbColor = TodoAppTheme.color.blue,
                checkedTrackColor = TodoAppTheme.color.blue.copy(
                    alpha = 0.5f
                ),
                checkedBorderColor = TodoAppTheme.color.blue,
                uncheckedIconColor = TodoAppTheme.color.elevated,
                uncheckedThumbColor = TodoAppTheme.color.elevated,
                uncheckedTrackColor = TodoAppTheme.color.overlay,
                uncheckedBorderColor = TodoAppTheme.color.overlay
            ),
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoItemScreenPreview() {
    val previewState = TodoItemScreenState(
        text = "Пожалуйста, поставьте максимальный балл",
        importance = Importance.High,
        hasDeadline = true,
        deadline = Date().time,
        datePickerExpanded = false,
        priorityMenuExpanded = false,
        loading = false,
        failed = false,
        errorMessage = ""
    )
    TodoAppTheme {
        Content(
            screenState = previewState,
            snackBarHostState = SnackbarHostState(),
            onEvent = { }
        )
    }
}