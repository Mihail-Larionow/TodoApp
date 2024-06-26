package com.michel.feature.screens.todolistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.custom.ImageCheckbox
import com.michel.core.ui.custom.SwipeItem
import com.michel.core.data.models.TodoItem
import com.michel.feature.screens.extensions.bottomShadow
import com.michel.feature.screens.todolistscreen.utils.ListScreenEvent

private val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
private val EXPANDED_TOP_BAR_HEIGHT = 200.dp

// Экран списка дел
@Composable
fun TodoListScreen(navigate: (String) -> Unit) {
    val viewModel: TodoListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Content(state = state) { event ->
        when(event) {
            is ListScreenEvent.ToItemScreenEvent -> navigate(event.id)
            else -> viewModel.onEvent(event)
        }
    }

}

@Composable
private fun Content(
    state: ListScreenState,
    onEvent: (ListScreenEvent) -> Unit,
) {
    val listState = rememberLazyListState()

    var doneItemsHide by remember { mutableStateOf(state.doneItemsHide) }

    var doneItemsCount by remember { mutableIntStateOf(state.doneItemsCount) }

    val todoItems = remember { state.todoItems.reversed().toMutableStateList() }

    val minOffsetCollapsed = with(LocalDensity.current) {
        (EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT).toPx()
    }

    val minOffsetCollapsing = with(LocalDensity.current) {
        ((EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT) * 2 / 3).toPx()
    }

    val isTopBarCollapsed: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > minOffsetCollapsed
        }
    }

    val isTopBarCollapsing: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > minOffsetCollapsing
        }
    }

    Box(
        modifier = Modifier
            .background(
                color = TodoAppTheme.color.backPrimary
            )
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                CollapsedToolBar(
                    isCollapsed = isTopBarCollapsed,
                    hideDoneItems = doneItemsHide,
                    onEvent = { event ->
                        doneItemsHide = event.isNotVisible
                        onEvent(event)
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val paddings = innerPadding
            Body(
                listState = listState,
                itemList = todoItems,
                doneItemsHide = doneItemsHide,
                doneItemsCount = doneItemsCount,
                isTopBarCollapsing = isTopBarCollapsing,
                onEvent = { event ->
                    when(event) {
                        is ListScreenEvent.ChangeVisibilityEvent -> {
                            doneItemsHide = event.isNotVisible
                            onEvent(event)
                        }
                        is ListScreenEvent.DeleteItemEvent -> {
                            if(event.item.isDone) doneItemsCount--
                            todoItems.remove(event.item)
                            onEvent(event)
                        }
                        is ListScreenEvent.UpdateItemEvent -> {
                            if(event.item.isDone) doneItemsCount++
                            else doneItemsCount--
                            onEvent(event)
                        }
                        else -> { onEvent(event) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
                    .padding(
                        start = 12.dp,
                        end = 12.dp
                    )

            )
        }
        FloatingButton(
            onClick = {
                onEvent(
                    ListScreenEvent.ToItemScreenEvent("${state.todoItems.last().id.toInt() + 1}")
                )
            },
            modifier = Modifier.align(
                Alignment.BottomEnd
            )
        )
    }
}

// Основное тело с тасками
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    itemList: List<TodoItem>,
    doneItemsHide: Boolean,
    doneItemsCount: Int,
    isTopBarCollapsing: Boolean,
    onEvent: (ListScreenEvent) -> Unit
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize()
        ) {
            item{
                ExpandedToolBar(
                    hideDoneItems = doneItemsHide,
                    count = doneItemsCount,
                    isCollapsing = isTopBarCollapsing,
                    onEvent = onEvent,
                    modifier = Modifier.background(
                        color = TodoAppTheme.color.backPrimary
                    )
                )
            }

            val showList = if(doneItemsHide) {
                itemList.filter { !it.isDone }
            } else {
                itemList
            }

            items(
                items = showList,
                key = { it.id },
            ) { item ->
                var isDone by remember {
                    mutableStateOf(item.isDone)
                }
                SwipeItem(
                    onRemove = {
                        onEvent(ListScreenEvent.DeleteItemEvent(item))
                    },
                    onDone = {
                        if(!isDone) {
                            onEvent(ListScreenEvent.UpdateItemEvent(item.copy(isDone = it)))
                            isDone = it
                        }
                    },
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(300)
                    )
                ) {
                    TodoItemModel(
                        todoItem = item,
                        checked = isDone,
                        onEvent = { event ->
                            when(event){
                                is ListScreenEvent.UpdateItemEvent -> {
                                    isDone = event.item.isDone
                                    onEvent(event)
                                }
                                else -> {
                                    onEvent(event)
                                }
                            }
                        }
                    )
                }
            }

            item {
                Text(
                    text = stringResource(com.michel.core.ui.R.string.new_task),
                    color = TodoAppTheme.color.tertiary,
                    style = TodoAppTheme.typography.body,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = TodoAppTheme.color.backSecondary
                        )
                        .clickable {
                            onEvent(ListScreenEvent.ToItemScreenEvent("${itemList.first().id.toInt() + 1}"))
                        }
                        .padding(
                            start = 32.dp + TodoAppTheme.size.standardIcon,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                        .animateItemPlacement(
                            animationSpec = tween(300)
                        )
                )
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
}

// Топ бар, когда он развернут
@Composable
private fun ExpandedToolBar(
    modifier: Modifier = Modifier,
    count: Int,
    hideDoneItems: Boolean,
    isCollapsing: Boolean,
    onEvent: (ListScreenEvent.ChangeVisibilityEvent) -> Unit,
) {
    val startPadding by animateDpAsState(
        targetValue = if(!isCollapsing) 56.dp else 4.dp,
        label = "Animated start padding"
    )

    val endPadding by animateDpAsState(
        targetValue = if(!isCollapsing) 20.dp else 4.dp,
        label = "Animated end padding"
    )

    val fontSize by animateIntAsState(
        targetValue = if(!isCollapsing) 32 else 20,
        label = "Animated font size"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT)
            .padding(
                start = startPadding,
                end = endPadding,
                bottom = 16.dp
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
                Text(
                    text = stringResource(com.michel.core.ui.R.string.todolist_screen_subtitle) + " $count",
                    style = TodoAppTheme.typography.subhead,
                    color = TodoAppTheme.color.tertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        VisibilityCheckBox(
            checked = hideDoneItems,
            onCheckChange = { onEvent(ListScreenEvent.ChangeVisibilityEvent(it)) },
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
    onEvent: (ListScreenEvent.ChangeVisibilityEvent) -> Unit,
) {

    val topBarColor = if(!isCollapsed){
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
        if(isCollapsed) {
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
                    onCheckChange = { onEvent(ListScreenEvent.ChangeVisibilityEvent(it)) },
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
    ){
        Row {
            FloatingActionButton(
                onClick = onClick,
                shape = CircleShape,
                containerColor = TodoAppTheme.color.blue,
                contentColor = TodoAppTheme.color.white,
                modifier = modifier.bottomShadow(
                    shadow = 4.dp,
                    shape = CircleShape
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
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit
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