package com.michel.feature.todolistscreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.custom.CustomPullToRefreshItem
import com.michel.core.ui.custom.ImageCheckbox
import com.michel.core.ui.custom.SwipeItem
import com.michel.core.ui.extensions.bottomShadow
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import com.michel.feature.todolistscreen.utils.ListScreenSideEffect
import com.michel.feature.todolistscreen.utils.TodoListScreenState

private val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
private val EXPANDED_TOP_BAR_HEIGHT = 200.dp

// Экран списка дел
@Composable
fun TodoListScreen(navigate: (String) -> Unit) {
    val viewModel: TodoListScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ListScreenSideEffect.ShowSnackBarSideEffect -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Content(
        screenState = state, snackBarHostState = snackBarHostState
    ) { event ->
        when (event) {
            is ListScreenIntent.ToItemScreenIntent -> navigate(event.id)
            else -> viewModel.onEvent(event)
        }
    }
}

@Composable
private fun Content(
    screenState: TodoListScreenState,
    snackBarHostState: SnackbarHostState,
    onEvent: (ListScreenIntent) -> Unit,
) {
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

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
        CollapsedToolBar(
            isCollapsed = isTopBarCollapsed,
            hideDoneItems = screenState.doneItemsHide,
            onEvent = onEvent,
        )
    }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val paddings = innerPadding
        Box(
            modifier = Modifier
                .background(
                    color = TodoAppTheme.color.backPrimary
                )
                .fillMaxSize()
        ) {

            Body(
                listState = listState,
                screenState = screenState,
                isTopBarCollapsing = isTopBarCollapsing,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
                    .padding(
                        start = 12.dp, end = 12.dp
                    )
            )

            FloatingButton(
                enabled = !screenState.loading && !screenState.failed, onClick = {
                    val id = if (screenState.todoItems.isNotEmpty()) {
                        screenState.todoItems.last().id.toInt() + 1
                    } else {
                        1
                    }
                    onEvent(
                        ListScreenIntent.ToItemScreenIntent("$id")
                    )
                }, modifier = Modifier.align(
                    Alignment.BottomEnd
                )
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
        }
    }
}

// Основное тело с тасками
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    screenState: TodoListScreenState,
    isTopBarCollapsing: Boolean,
    onEvent: (ListScreenIntent) -> Unit
) {

    CustomPullToRefreshItem(isLoading = screenState.loading,
        onRefresh = { onEvent(ListScreenIntent.GetItemsIntent) },
        content = {
            LazyColumn(
                state = listState, modifier = modifier.fillMaxSize()
            ) {
                item {
                    ExpandedToolBar(
                        screenState = screenState,
                        isCollapsing = isTopBarCollapsing,
                        onEvent = onEvent,
                        modifier = Modifier.background(
                            color = TodoAppTheme.color.backPrimary
                        )
                    )
                }

                val showList = if (screenState.doneItemsHide) {
                    screenState.todoItems.filter { !it.isDone }
                } else {
                    screenState.todoItems
                }

                items(
                    items = showList.reversed(),
                    key = { it.id },
                ) { item ->
                    SwipeItem(onDelete = {
                        onEvent(ListScreenIntent.DeleteItemIntent(item))
                    }, onUpdate = {
                        onEvent(ListScreenIntent.UpdateItemIntent(item.copy(isDone = it)))
                    }, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(300)
                    )
                    ) {
                        TodoItemModel(
                            todoItem = item, checked = item.isDone, onEvent = onEvent
                        )
                    }
                }

                item {
                    if (!screenState.failed) {
                        Text(text = stringResource(com.michel.core.ui.R.string.new_task),
                            color = TodoAppTheme.color.tertiary,
                            style = TodoAppTheme.typography.body,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = TodoAppTheme.color.backSecondary
                                )
                                .clickable {
                                    val id = if (screenState.todoItems.isNotEmpty()) {
                                        screenState.todoItems.last().id.toInt() + 1
                                    } else {
                                        1
                                    }
                                    onEvent(
                                        ListScreenIntent.ToItemScreenIntent("$id")
                                    )
                                }
                                .padding(
                                    start = 32.dp + TodoAppTheme.size.standardIcon,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                                .animateItemPlacement(
                                    animationSpec = tween(300)
                                ))
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .background(
                                color = TodoAppTheme.color.backPrimary
                            )
                    )
                }
            }
        }
    )
}

// Топ бар, когда он развернут
@Composable
private fun ExpandedToolBar(
    modifier: Modifier = Modifier,
    screenState: TodoListScreenState,
    isCollapsing: Boolean,
    onEvent: (ListScreenIntent.ChangeVisibilityIntent) -> Unit,
) {
    val startPadding by animateDpAsState(
        targetValue = if (!isCollapsing) 56.dp else 4.dp, label = "Animated start padding"
    )

    val endPadding by animateDpAsState(
        targetValue = if (!isCollapsing) 20.dp else 4.dp, label = "Animated end padding"
    )

    val fontSize by animateIntAsState(
        targetValue = if (!isCollapsing) 32 else 20, label = "Animated font size"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT)
            .padding(
                start = startPadding, end = endPadding, bottom = 16.dp
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = stringResource(com.michel.core.ui.R.string.todolist_screen_title),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = TodoAppTheme.color.primary,
                modifier = Modifier
            )
            AnimatedVisibility(visible = !isCollapsing) {
                val countText =
                    stringResource(com.michel.core.ui.R.string.todolist_screen_subtitle) + " ${screenState.doneItemsCount}"
                Text(
                    text = countText,
                    style = TodoAppTheme.typography.subhead,
                    color = TodoAppTheme.color.tertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        VisibilityCheckBox(
            checked = screenState.doneItemsHide,
            onCheckChange = { onEvent(ListScreenIntent.ChangeVisibilityIntent(it)) },
            modifier = Modifier
                .size(TodoAppTheme.size.standardIcon)
                .align(Alignment.BottomEnd)
        )
    }
}

// Топ бар, когда он сжат
@Composable
private fun CollapsedToolBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    hideDoneItems: Boolean,
    onEvent: (ListScreenIntent.ChangeVisibilityIntent) -> Unit,
) {

    val topBarColor = if (!isCollapsed) {
        Color.Transparent
    } else {
        TodoAppTheme.color.backPrimary
    }

    val shadow = if (isCollapsed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .bottomShadow(
                shadow = shadow
            )
            .background(
                color = topBarColor
            )
            .padding(
                all = 16.dp
            )
    ) {
        if (isCollapsed) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(com.michel.core.ui.R.string.todolist_screen_title),
                    style = TodoAppTheme.typography.title,
                    color = TodoAppTheme.color.primary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                VisibilityCheckBox(
                    checked = hideDoneItems,
                    onCheckChange = { onEvent(ListScreenIntent.ChangeVisibilityIntent(it)) },
                    modifier = Modifier
                        .size(TodoAppTheme.size.standardIcon)
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

// Кнопка
@Composable
private fun FloatingButton(
    modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row {
            FloatingActionButton(
                onClick = { if (enabled) onClick() },
                shape = CircleShape,
                containerColor = TodoAppTheme.color.blue,
                contentColor = TodoAppTheme.color.white,
                modifier = modifier.bottomShadow(
                    shadow = 4.dp, shape = CircleShape
                )

            ) {
                Icon(
                    painter = painterResource(com.michel.core.ui.R.drawable.ic_add),
                    contentDescription = stringResource(com.michel.core.ui.R.string.floatingButtonContentDescription),
                    modifier = Modifier.size(TodoAppTheme.size.standardIcon)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Чекбокс глаз
@Composable
private fun VisibilityCheckBox(
    modifier: Modifier = Modifier, checked: Boolean, onCheckChange: (Boolean) -> Unit
) {
    val checkedIcon = painterResource(com.michel.core.ui.R.drawable.ic_visibility_off)
    val uncheckedIcon = painterResource(com.michel.core.ui.R.drawable.ic_visibility_on)

    ImageCheckbox(
        checked = checked,
        checkedIcon = checkedIcon,
        uncheckedIcon = uncheckedIcon,
        onCheckedChange = { onCheckChange(it) },
        modifier = modifier
    )
}

private val list = listOf(
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

private val state = TodoListScreenState(
    todoItems = list,
    doneItemsHide = false,
    doneItemsCount = 1,
    failed = false,
    loading = false,
    errorMessage = ""
)

@Preview(showBackground = true)
@Composable
private fun TodoListScreenPreviewLight() {
    TodoAppTheme {
        Content(
            screenState = state,
            snackBarHostState = SnackbarHostState(),
            onEvent = { })
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListScreenPreviewDark() {
    TodoAppTheme {
        Content(
            screenState = state,
            snackBarHostState = SnackbarHostState(),
            onEvent = { })
    }
}