package com.michel.feature.todolistscreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.custom.CustomCollapsingLabel
import com.michel.core.ui.custom.CustomPullToRefreshItem
import com.michel.core.ui.custom.CustomSnackBarHost
import com.michel.core.ui.custom.ImageCheckbox
import com.michel.core.ui.custom.SwipeItem
import com.michel.core.ui.extensions.bottomShadow
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.todolistscreen.utils.ListScreenEffect
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
private val EXPANDED_TOP_BAR_HEIGHT = 200.dp

/**
 * Contains UI implementation of items list screen
 */
@Composable
fun TodoListScreen(navigate: (String) -> Unit) {
    val viewModel: TodoListScreenViewModel = hiltViewModel()
    val snackBarHostState = remember { SnackbarHostState() }
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    val minOffsetCollapsed = with(LocalDensity.current) {
        (EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT).toPx()
    }

    val minOffsetCollapsing = with(LocalDensity.current) {
        ((EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT) * 2 / 3).toPx()
    }

    val isTopBarCollapsed: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > minOffsetCollapsed
        }
    }

    val isTopBarCollapsing: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > minOffsetCollapsing
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        collectSideEffects(
            coroutineScope = scope,
            viewModel = viewModel,
            snackBarHostState = snackBarHostState,
            navigate = navigate,
            onEvent = viewModel::handleIntent
        )
    }

    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(snackBarHostState)
        }, topBar = {
            CollapsedToolBar(
                isCollapsed = isTopBarCollapsed,
                screenState = screenState,
                onEvent = viewModel::handleIntent,
            )
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val paddings = innerPadding
        Content(
            screenState = screenState,
            listState = listState,
            isCollapsing = isTopBarCollapsing,
            onEvent = viewModel::handleIntent
        )
    }
}

@Composable
private fun Content(
    screenState: ListScreenState,
    listState: LazyListState,
    isCollapsing: Boolean,
    onEvent: (ListScreenIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TodoAppTheme.color.backPrimary)
    ) {
        CustomPullToRefreshItem(
            isRefreshing = screenState.isRefreshing,
            onRefresh = {
                onEvent(ListScreenIntent.StartLoadingIntent)
                onEvent(ListScreenIntent.GetItemsIntent)
            }
        ) {
            Body(
                listState = listState,
                screenState = screenState,
                isTopBarCollapsing = isCollapsing,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = TodoAppTheme.color.backPrimary)
                    .padding(start = 12.dp, end = 12.dp)
            )
        }
        FloatingButton(
            enabled = screenState.enabled,
            onClick = { onEvent(ListScreenIntent.ToItemScreenIntent("none")) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
        AnimatedSettingsIcon(
            onEvent = onEvent,
            isCollapsing = isCollapsing,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 8.dp)
        )
        AnimatedVisibility(
            visible = !screenState.isRefreshing && screenState.failed && screenState.todoItems.isEmpty(),
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            ErrorContent(
                screenState = screenState,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun AnimatedSettingsIcon(
    modifier: Modifier = Modifier,
    onEvent: (ListScreenIntent) -> Unit,
    isCollapsing: Boolean,
) {
    AnimatedVisibility(
        visible = !isCollapsing,
        exit = scaleOut(),
        enter = scaleIn(),
        modifier = modifier,
    ) {
        IconButton(onClick = { onEvent(ListScreenIntent.ToItemScreenIntent("none")) }) {
            Icon(
                tint = TodoAppTheme.color.tertiary,
                painter = painterResource(id = com.michel.core.ui.R.drawable.ic_settings),
                contentDescription = stringResource(id = com.michel.core.ui.R.string.settingsContentDescription)
            )
        }
    }
}

/**
 * The main body of the screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    screenState: ListScreenState,
    isTopBarCollapsing: Boolean,
    onEvent: (ListScreenIntent) -> Unit
) {
    val showList = if (screenState.doneItemsHide) {
        screenState.todoItems.filter { !it.isDone }
    } else {
        screenState.todoItems
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            ExpandedToolBar(
                screenState = screenState,
                isCollapsing = isTopBarCollapsing,
                onEvent = onEvent,
                modifier = Modifier.background(color = TodoAppTheme.color.backPrimary)
            )
        }

        items(
            items = showList.reversed(),
            key = { it.id },
        ) { item ->
            TodoListItem(
                todoItem = item,
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier.animateItemPlacement()
            )
        }

        item {
            NewButton(
                screenState = screenState,
                onEvent = onEvent,
                modifier = Modifier.animateItemPlacement()
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .background(color = TodoAppTheme.color.backPrimary)
            )
        }
    }
}

@Composable
private fun TodoListItem(
    modifier: Modifier = Modifier,
    todoItem: TodoItem,
    screenState: ListScreenState,
    onEvent: (ListScreenIntent) -> Unit
) {
    SwipeItem(
        onDelete = { onEvent(ListScreenIntent.DeleteItemIntent(todoItem)) },
        onUpdate = { onEvent(ListScreenIntent.UpdateItemIntent(todoItem.copy(isDone = it))) },
        modifier = modifier
    ) {
        TodoItemModel(
            todoItem = todoItem,
            checked = todoItem.isDone,
            enabled = screenState.enabled,
            onEvent = onEvent
        )
    }
}

@Composable
private fun NewButton(
    modifier: Modifier = Modifier,
    screenState: ListScreenState,
    onEvent: (ListScreenIntent) -> Unit
) {
    val buttonColor = animateColorAsState(
        targetValue = if (screenState.enabled) {
            TodoAppTheme.color.backSecondary
        } else {
            TodoAppTheme.color.disable
        }, label = ""
    )

    if (!screenState.failed) {
        Text(
            text = stringResource(com.michel.core.ui.R.string.new_task),
            color = TodoAppTheme.color.tertiary,
            style = TodoAppTheme.typography.body,
            modifier = modifier
                .fillMaxWidth()
                .background(color = buttonColor.value)
                .clickable(
                    enabled = screenState.enabled,
                    onClick = { onEvent(ListScreenIntent.ToItemScreenIntent("none")) }
                )
                .padding(
                    start = 32.dp + TodoAppTheme.size.standardIcon,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )
    }
}

@Composable
private fun ErrorContent(
    screenState: ListScreenState,
    onEvent: (ListScreenIntent) -> Unit
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
            onClick = { onEvent(ListScreenIntent.GetItemsIntent) },
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

// Топ бар, когда он развернут
@Composable
private fun ExpandedToolBar(
    modifier: Modifier = Modifier,
    screenState: ListScreenState,
    isCollapsing: Boolean,
    onEvent: (ListScreenIntent.ChangeVisibilityIntent) -> Unit,
) {
    val startPadding by animateDpAsState(
        targetValue = if (!isCollapsing) 56.dp else 4.dp, label = "Animated start padding"
    )

    val endPadding by animateDpAsState(
        targetValue = if (!isCollapsing) 20.dp else 4.dp, label = "Animated end padding"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT)
            .padding(
                start = startPadding, end = endPadding, bottom = 16.dp
            )
    ) {
        TodoText(
            isCollapsing = isCollapsing,
            screenState = screenState,
            itemCount = screenState.doneItemsCount,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        VisibilityCheckBox(
            screenState = screenState,
            onCheckChange = { onEvent(ListScreenIntent.ChangeVisibilityIntent(it)) },
            modifier = Modifier
                .size(TodoAppTheme.size.standardIcon)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun TodoText(
    modifier: Modifier = Modifier,
    screenState: ListScreenState,
    isCollapsing: Boolean,
    itemCount: Int
) {
    Column(
        modifier = modifier
    ) {
        val fontSize by animateIntAsState(
            targetValue = if (!isCollapsing) 32 else 20,
            label = "Animated font size"
        )

        Row {
            Text(
                text = stringResource(com.michel.core.ui.R.string.todolist_screen_title),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = TodoAppTheme.color.primary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            AnimatedNetworkLabel(
                screenState = screenState,
                isCollapsing = isCollapsing
            )
        }

        AnimatedVisibility(
            visible = !isCollapsing,
        ) {
            val countText =
                stringResource(com.michel.core.ui.R.string.todolist_screen_subtitle) + " $itemCount"
            Text(
                text = countText,
                style = TodoAppTheme.typography.subhead,
                color = TodoAppTheme.color.tertiary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun AnimatedNetworkLabel(
    screenState: ListScreenState,
    isCollapsing: Boolean,
) {
    val labelColor by animateColorAsState(
        targetValue = if (screenState.isConnected) {
            TodoAppTheme.color.green
        } else {
            TodoAppTheme.color.red
        },
        label = "Animated color"
    )

    val text = if (screenState.isConnected) "online" else "offline"

    AnimatedVisibility(
        visible = !isCollapsing,
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start),
        enter = expandHorizontally(expandFrom = Alignment.Start)
    ) {
        CustomCollapsingLabel(
            containerColor = labelColor,
            containerShape = TodoAppTheme.shape.container,
            textStyle = TextStyle(fontSize = 10.sp),
            textColor = TodoAppTheme.color.white,
            state = screenState.isConnected,
            text = text,
            size = 12.dp
        )
    }
}

/**
 * Top Bar when it's collapsed.
 */
@Composable
private fun CollapsedToolBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    screenState: ListScreenState,
    onEvent: (ListScreenIntent.ChangeVisibilityIntent) -> Unit,
) {
    val topBarColor = if (!isCollapsed) {
        Color.Transparent
    } else {
        TodoAppTheme.color.backPrimary
    }

    val shadow = if (isCollapsed) 4.dp else 0.dp

    if (isCollapsed) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(COLLAPSED_TOP_BAR_HEIGHT)
                .bottomShadow(shadow = shadow)
                .background(color = topBarColor)
                .padding(all = 16.dp)
        ) {
            Text(
                text = stringResource(com.michel.core.ui.R.string.todolist_screen_title),
                style = TodoAppTheme.typography.title,
                color = TodoAppTheme.color.primary,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            VisibilityCheckBox(
                screenState = screenState,
                onCheckChange = { onEvent(ListScreenIntent.ChangeVisibilityIntent(it)) },
                modifier = Modifier
                    .size(TodoAppTheme.size.standardIcon)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

/**
 * Floating App Button.
 */
@Composable
private fun FloatingButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = { if (enabled) onClick() },
            shape = CircleShape,
            containerColor = TodoAppTheme.color.blue,
            contentColor = TodoAppTheme.color.white,
            modifier = modifier.bottomShadow(shadow = 4.dp, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(com.michel.core.ui.R.drawable.ic_add),
                contentDescription = stringResource(com.michel.core.ui.R.string.floatingButtonContentDescription),
                modifier = Modifier.size(TodoAppTheme.size.standardIcon)
            )
        }
    }
}

/**
 * Visibility checkbox.
 */
@Composable
private fun VisibilityCheckBox(
    modifier: Modifier = Modifier,
    screenState: ListScreenState,
    onCheckChange: (Boolean) -> Unit
) {
    val checkedIcon = painterResource(com.michel.core.ui.R.drawable.ic_visibility_off)
    val uncheckedIcon = painterResource(com.michel.core.ui.R.drawable.ic_visibility_on)

    ImageCheckbox(
        checked = screenState.doneItemsHide,
        checkedIcon = checkedIcon,
        checkedTint = TodoAppTheme.color.blue,
        uncheckedIcon = uncheckedIcon,
        uncheckedTint = TodoAppTheme.color.blue,
        disabledTint = TodoAppTheme.color.disable,
        onCheckedChange = { onCheckChange(it) },
        enabled = screenState.enabled,
        modifier = modifier
    )
}

/**
 * Handles side effects.
 */
private suspend fun collectSideEffects(
    coroutineScope: CoroutineScope,
    viewModel: TodoListScreenViewModel,
    snackBarHostState: SnackbarHostState,
    navigate: (String) -> Unit,
    onEvent: (ListScreenIntent) -> Unit
) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is ListScreenEffect.LeaveScreenEffect -> navigate(effect.id)
            is ListScreenEffect.ShowSimpleSnackBarEffect -> showSimpleSnackBar(
                coroutineScope = coroutineScope,
                snackBarHostState = snackBarHostState,
                message = effect.message
            )

            is ListScreenEffect.ShowButtonSnackBarEffect -> showButtonSnackBar(
                coroutineScope = coroutineScope,
                snackBarHostState = snackBarHostState,
                message = effect.message,
                buttonText = effect.actionText,
                onClick = { onEvent(ListScreenIntent.GetItemsIntent) }
            )
        }
    }
}

private fun showSimpleSnackBar(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String
) {
    coroutineScope.launch {
        snackBarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}

private fun showButtonSnackBar(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    buttonText: String,
    onClick: () -> Unit
) {
    coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            message = message,
            actionLabel = buttonText,
            duration = SnackbarDuration.Short,
        )
        when (result) {
            SnackbarResult.Dismissed -> {}
            SnackbarResult.ActionPerformed -> onClick()
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListScreenPreview() {
    val list = listOf(
        TodoItem(
            id = "6",
            text = "Устроиться работать в пятерочку(",
            importance = Importance.Low,
            isDone = false,
            createdAt = 1
        ),
        TodoItem(
            id = "7",
            text = "Устроиться работать в Яндикс)",
            importance = Importance.High,
            isDone = false,
            createdAt = 2
        ),
        TodoItem(
            id = "8",
            text = "Выполненное задание",
            importance = Importance.Low,
            isDone = true,
            createdAt = 3
        ),
    )

    val state = ListScreenState(
        todoItems = list,
        doneItemsHide = false,
        doneItemsCount = 1,
        failed = false,
        isRefreshing = false,
        enabled = false,
        errorMessage = ""
    )

    TodoAppTheme {
        Content(
            screenState = state,
            listState = LazyListState(),
            isCollapsing = false,
            onEvent = { }
        )
    }
}