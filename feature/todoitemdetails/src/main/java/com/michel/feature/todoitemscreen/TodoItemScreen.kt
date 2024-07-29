package com.michel.feature.todoitemscreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.data.models.Importance
import com.michel.core.ui.custom.CustomBottomSheet
import com.michel.core.ui.custom.CustomDatePicker
import com.michel.core.ui.custom.CustomSwitch
import com.michel.core.ui.custom.CustomTextField
import com.michel.core.ui.custom.TodoDivider
import com.michel.core.ui.extensions.bottomShadow
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.ripple.ColoredRippleTheme
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.todoitemscreen.utils.ItemScreenEffect
import com.michel.feature.todoitemscreen.utils.ItemScreenIntent
import com.michel.feature.todoitemscreen.utils.ItemScreenState
import java.util.Date

private val TOP_BAR_HEIGHT = 56.dp

/**
 * Contains UI implementation of item screen
 */
@Composable
fun TodoItemScreen(
    snackBarHostState: SnackbarHostState,
    snackBarHost: @Composable () -> Unit,
    navigate: () -> Unit
) {
    val viewModel: TodoItemScreenViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectSideEffects(
            viewModel = viewModel,
            snackBarHostState = snackBarHostState,
            navigate = navigate
        )
    }

    val listState = rememberLazyListState()

    val showShadow: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset > 0 || listState.firstVisibleItemIndex > 0
        }
    }

    val headerShadow = if (showShadow) 4.dp else 0.dp

    Scaffold(
        snackbarHost = snackBarHost,
        topBar = {
            Header(
                screenState = screenState,
                onEvent = viewModel::handleIntent,
                modifier = Modifier
                    .height(TodoAppTheme.size.toolBar)
                    .bottomShadow(shadow = headerShadow)
                    .background(color = TodoAppTheme.color.backPrimary)
                    .padding(
                        start = 16.dp,
                        end = 12.dp,
                        bottom = 4.dp,
                        top = 4.dp
                    )
                    .semantics {
                        isTraversalGroup = true
                    }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = TodoAppTheme.color.backPrimary)
    ) { innerPadding ->
        Content(
            screenState = screenState,
            listState = listState,
            onEvent = viewModel::handleIntent,
            modifier = Modifier.padding(paddingValues = innerPadding)
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    screenState: ItemScreenState,
    listState: LazyListState,
    onEvent: (ItemScreenIntent) -> Unit,
) {
    Body(
        listState = listState,
        screenState = screenState,
        onEvent = onEvent,
        modifier = modifier
    )

    AnimatedVisibility(
        visible = screenState.priorityMenuExpanded,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        ImportanceBottomSheet(
            screenState = screenState,
            onEvent = onEvent
        )
    }

    AnimatedVisibility(visible = screenState.failed) {
        ErrorContent(
            screenState = screenState,
            onEvent = onEvent
        )
    }

    AnimatedVisibility(visible = screenState.datePickerExpanded) {
        TodoDatePicker(
            date = screenState.deadline,
            onEvent = onEvent
        )
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val textColor = animateColorAsState(
        targetValue =
        if (screenState.text == "" || !screenState.enabled) {
            TodoAppTheme.color.disable
        } else {
            TodoAppTheme.color.blue
        }, label = ""
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TOP_BAR_HEIGHT)
            .padding(start = 8.dp, end = 8.dp)
    ) {
        IconButton(
            onClick = { onEvent(ItemScreenIntent.ToListScreenIntent) },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .semantics {
                    traversalIndex = -1f
                }
        ) {
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_exit),
                contentDescription = stringResource(com.michel.core.ui.R.string.exit),
                tint = TodoAppTheme.color.primary,
                modifier = Modifier.size(TodoAppTheme.size.standardIcon)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            enabled = screenState.text != "" && screenState.enabled,
            onClick = { onEvent(ItemScreenIntent.SaveIntent) },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .testTag("save_task_button")
        ) {
            Text(
                text = stringResource(com.michel.core.ui.R.string.saveUpperCase),
                color = textColor.value,
                style = TodoAppTheme.typography.button
            )
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(color = TodoAppTheme.color.backPrimary)
    ) {
        item {
            CustomTextField(
                text = screenState.text,
                shape = TodoAppTheme.shape.container,
                enabled = screenState.enabled,
                onValueChanged = { onEvent(ItemScreenIntent.SetTextIntent(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
                    .testTag("task_text_field")
            )
        }
        item {
            ImportanceField(
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier
            )
            TodoDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        }
        item {
            DeadlineField(
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier.padding(all = 16.dp)
            )
            TodoDivider()
        }
        item {
            DeleteButton(
                screenState = screenState,
                onClick = onEvent
            )
        }
    }
}

@Composable
private fun ImportanceBottomSheet(
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val options = listOf(
        Importance.High,
        Importance.Basic,
        Importance.Low
    )

    CustomBottomSheet(
        onDismiss = { onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(false)) }
    ) {
        TodoDivider()
        options.forEach {
            ImportanceItem(
                isSelected = screenState.importance == it,
                importance = it,
                onClick = {
                    onEvent(ItemScreenIntent.SetPriorityIntent(it))
                    onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(false))
                }
            )
            TodoDivider()
        }
        Spacer(modifier = Modifier.height(TodoAppTheme.size.toolBar))
    }
}

@Composable
private fun ImportanceItem(
    isSelected: Boolean,
    importance: Importance,
    onClick: () -> Unit,
) {
    val textColor = if (importance == Importance.High) {
        TodoAppTheme.color.red
    } else {
        TodoAppTheme.color.secondary
    }

    val rippleColor = if (importance == Importance.High) {
        TodoAppTheme.color.red
    } else {
        TodoAppTheme.color.disable
    }

    CompositionLocalProvider(LocalRippleTheme provides ColoredRippleTheme(rippleColor)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
                .clearAndSetSemantics {
                    role = Role.RadioButton
                    selected = isSelected
                    contentDescription = importance.text
                }
        ) {
            Text(
                text = importance.text,
                style = TodoAppTheme.typography.title,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
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
                text = stringResource(com.michel.core.ui.R.string.retryUpperCase),
                color = TodoAppTheme.color.red,
                style = TodoAppTheme.typography.button
            )
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
private fun ImportanceField(
    modifier: Modifier = Modifier,
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = screenState.enabled,
                onClick = { onEvent(ItemScreenIntent.SetPriorityMenuStateIntent(true)) }
            )
            .padding(all = 16.dp)

    ) {
        val headColor by animateColorAsState(
            targetValue = if (screenState.enabled) {
                TodoAppTheme.color.primary
            } else {
                TodoAppTheme.color.disable
            }, label = ""
        )
        val importanceColor by animateColorAsState(
            targetValue = if (!screenState.enabled) {
                TodoAppTheme.color.disable
            } else if (screenState.importance == Importance.High) {
                TodoAppTheme.color.red
            } else {
                TodoAppTheme.color.tertiary
            }, label = ""
        )

        Text(
            text = stringResource(com.michel.core.ui.R.string.priority),
            color = headColor,
            style = TodoAppTheme.typography.body
        )
        Spacer(
            modifier = Modifier.height(4.dp)
        )
        Text(
            text = screenState.importance.text,
            color = importanceColor,
            style = TodoAppTheme.typography.body
        )
    }
}

@Composable
private fun DeadlineField(
    modifier: Modifier = Modifier,
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val switchStateDescription = if (screenState.hasDeadline) {
        stringResource(com.michel.core.ui.R.string.deadline_switch_has_deadline)
    } else {
        stringResource(com.michel.core.ui.R.string.deadline_switch_has_not_deadline)
    }

    Row(
        modifier = modifier
    ) {
        DeadlineColumn(
            screenState = screenState,
            onEvent = onEvent
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        CustomSwitch(
            hasDeadline = screenState.hasDeadline,
            enabled = screenState.enabled,
            onCheckChange = { onEvent(ItemScreenIntent.SetDeadlineStateIntent(it)) },
            modifier = Modifier.semantics {
                role = Role.Switch
                stateDescription = switchStateDescription
            }
        )
    }
}

@Composable
private fun DeadlineColumn(
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val headColor = animateColorAsState(
        targetValue = if (screenState.enabled) {
            TodoAppTheme.color.primary
        } else {
            TodoAppTheme.color.disable
        }, label = ""
    )
    Column {
        Text(
            text = stringResource(com.michel.core.ui.R.string.do_before),
            color = headColor.value,
            style = TodoAppTheme.typography.body
        )
        AnimatedDeadlineText(
            screenState = screenState,
            onEvent = onEvent
        )
    }
}

@Composable
private fun AnimatedDeadlineText(
    screenState: ItemScreenState,
    onEvent: (ItemScreenIntent) -> Unit
) {
    val subheadColor = animateColorAsState(
        targetValue = if (screenState.enabled) {
            TodoAppTheme.color.blue
        } else {
            TodoAppTheme.color.disable
        }, label = ""
    )

    val buttonContentDescription =
        stringResource(com.michel.core.ui.R.string.date_content_description)

    Spacer(
        modifier = Modifier.height(4.dp)
    )
    AnimatedVisibility(visible = screenState.hasDeadline) {
        Text(
            text = screenState.deadlineDateText,
            color = subheadColor.value,
            style = TodoAppTheme.typography.subhead,
            modifier = Modifier
                .clickable(
                    enabled = screenState.enabled,
                    onClick = { onEvent(ItemScreenIntent.SetDatePickerStateIntent(true)) }
                )
                .semantics {
                    role = Role.Button
                    stateDescription = screenState.deadlineDateText
                    contentDescription = buttonContentDescription
                }
        )
    }
}

@Composable
private fun DeleteButton(
    screenState: ItemScreenState,
    onClick: (ItemScreenIntent) -> Unit
) {
    val buttonContentDescription =
        stringResource(com.michel.core.ui.R.string.deleteContentDescription)
    CompositionLocalProvider(LocalRippleTheme provides ColoredRippleTheme(TodoAppTheme.color.red)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = screenState.text != "" && screenState.enabled && screenState.deleteButtonEnabled,
                    onClick = { onClick(ItemScreenIntent.DeleteIntent) }
                )
                .padding(all = 16.dp)
                .clearAndSetSemantics {
                    role = Role.Button
                    contentDescription = buttonContentDescription
                }
        ) {
            val buttonColor =
                if (screenState.text == "" || !screenState.enabled || !screenState.deleteButtonEnabled) {
                    TodoAppTheme.color.disable
                } else {
                    TodoAppTheme.color.red
                }
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_delete),
                tint = buttonColor,
                contentDescription = buttonContentDescription,
                modifier = Modifier
                    .size(TodoAppTheme.size.standardIcon)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(com.michel.core.ui.R.string.deleteUpperCase),
                color = buttonColor,
                style = TodoAppTheme.typography.button,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

// Собирает сайд эффекты
private suspend fun collectSideEffects(
    viewModel: TodoItemScreenViewModel,
    snackBarHostState: SnackbarHostState,
    navigate: () -> Unit
) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is ItemScreenEffect.ShowSnackBarEffect -> snackBarHostState.showSnackbar(effect.message)
            ItemScreenEffect.LeaveScreenEffect -> navigate()
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoItemScreenPreview() {
    val date = Date().time
    val previewState = ItemScreenState(
        text = "Пожалуйста, поставьте максимальный балл",
        importance = Importance.High,
        hasDeadline = true,
        deadline = date,
        deadlineDateText = date.toDateText(),
        datePickerExpanded = false,
        priorityMenuExpanded = false,
        loading = false,
        failed = false,
        enabled = true,
        deleteButtonEnabled = true,
        errorMessage = ""
    )
    TodoAppTheme {
        Content(
            screenState = previewState,
            listState = LazyListState(),
            onEvent = { }
        )
    }
}