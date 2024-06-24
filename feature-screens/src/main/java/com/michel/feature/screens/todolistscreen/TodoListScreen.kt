package com.michel.feature.screens.todolistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.utils.ImageCheckbox
import com.michel.core.ui.utils.SwipeItem
import com.michel.feature.screens.extensions.bottomShadow

private val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
private val EXPANDED_TOP_BAR_HEIGHT = 200.dp

// Экран списка дел
@Composable
internal fun TodoListScreen(navigate: (String) -> Unit) {
    val viewModel: TodoListViewModel = hiltViewModel()
    Content(viewModel = viewModel) {
        navigate(it)
    }
}

@Composable
private fun Content(
    viewModel: TodoListViewModel = hiltViewModel(),
    onItemClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    var hideDoneItems by remember {
        mutableStateOf(viewModel.isHidingItems())
    }

    var doneItemsCount by remember {
        mutableIntStateOf(
            viewModel.getItems().count {
                it.isDone
            }
        )
    }

    val isCollapsed: Boolean by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
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
                    isCollapsed = isCollapsed,
                    hideDoneItems = hideDoneItems,
                    onCheckChange = {
                        hideDoneItems = it
                        viewModel.changeCheckState(it)
                    },
                    modifier = Modifier.background(
                        color = if(!isCollapsed){
                            Color.Transparent
                        } else {
                            TodoAppTheme.color.backPrimary
                        }
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Body(
                state = listState,
                itemList = viewModel.getItems(),
                hideDoneItems = hideDoneItems,
                doneItemsCount = doneItemsCount,
                onVisibilityChanged = {
                    hideDoneItems = it
                    viewModel.changeCheckState(it)
                },
                onDelete = {
                    viewModel.deleteItem(item = it)
                },
                onItemClick = onItemClick,
                onItemLongClick = { }, // Здесь должен был быть дроп даун меню...
                increaseDoneItems = {
                    doneItemsCount += it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
                    .padding(
                        paddingValues = innerPadding
                    )
                    .padding(
                        start = 12.dp,
                        end = 12.dp
                    )

            )
        }
        FloatingButton(
            onClick = {
                onItemClick(
                    (viewModel.getItems().last().id.toInt() + 1).toString()
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
    state: LazyListState,
    modifier: Modifier = Modifier,
    itemList: List<TodoItem>,
    hideDoneItems: Boolean,
    doneItemsCount: Int,
    onVisibilityChanged: (Boolean) -> Unit,
    onDelete: (TodoItem) -> Unit,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit,
    increaseDoneItems: (Int) -> Unit,
) {
    val todoItems = remember {
        itemList.reversed().toMutableStateList()
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            state = state,
            modifier = modifier.fillMaxSize()
        ) {
            item{
                ExpandedToolBar(
                    count = doneItemsCount,
                    hideDoneItems = hideDoneItems,
                    onCheckChange = onVisibilityChanged,
                    modifier = Modifier.background(
                        color = TodoAppTheme.color.backPrimary
                    )
                )
            }

            items(
                items = todoItems,
                key = { it.id },
            ) { item ->
                var isDone by remember {
                    mutableStateOf(item.isDone)
                }

                if(!hideDoneItems || !isDone) {
                    SwipeItem(
                        onRemove = {
                            if(isDone) increaseDoneItems(-1)
                            onDelete(item)
                            todoItems.remove(item)
                        },
                        onDone = {
                            if(!isDone && it) increaseDoneItems(1)
                            isDone = it
                            item.isDone = it
                        },
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(500)
                        )
                    ) {
                        TodoItemModel(
                            todoItem = item,
                            checked = isDone,
                            onCheckBoxClick = {
                                if(it) increaseDoneItems(1)
                                else increaseDoneItems(-1)
                                isDone = it
                                item.isDone = it
                            },
                            onClick = onItemClick,
                            onLongClick = onItemLongClick
                        )
                    }
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
                            onItemClick(
                                "${itemList.last().id.toInt() + 1}"
                            )
                        }
                        .padding(
                            start = 32.dp + TodoAppTheme.size.standardIcon,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
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
    onCheckChange: (check: Boolean) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT)
            .padding(
                start = 56.dp,
                end = 20.dp,
                bottom = 20.dp
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = stringResource(com.michel.core.ui.R.string.todolist_screen_title),
                style = TodoAppTheme.typography.largeTitle,
                color = TodoAppTheme.color.primary,
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text(
                text = stringResource(com.michel.core.ui.R.string.todolist_screen_subtitle) + " $count",
                style = TodoAppTheme.typography.subhead,
                color = TodoAppTheme.color.tertiary,
            )
        }
        VisibilityCheckBox(
            checked = hideDoneItems,
            onCheckChange = { onCheckChange(it) },
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
    onCheckChange: (check: Boolean) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .bottomShadow(
                shadow = if (isCollapsed) 4.dp else 0.dp
            )
            .background(
                color = TodoAppTheme.color.backPrimary
            )
            .padding(
                all = 16.dp
            )
    ) {
        AnimatedVisibility(
            visible = isCollapsed,
            exit = scaleOut(
                targetScale = 0.5f,
            ),
            enter = scaleIn(
                initialScale = 0.5f,
            )
        ) {
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
                    onCheckChange = { onCheckChange(it) },
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

// Хотел реализовать дроп даун меню при долгом клике,
// но не успел...
// (это был доп задание)
@Composable
private fun ItemDropdownMenu(
    modifier: Modifier = Modifier,
    onOptionSelect: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        stringResource(com.michel.core.ui.R.string.delete),
        stringResource(com.michel.core.ui.R.string.done)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(
            shape = RectangleShape,
            color = TodoAppTheme.color.elevated
        )
    ) {
        options.forEach {
            DropdownMenuItem(
                text = {
                    Text(
                        text = it,
                        color = TodoAppTheme.color.primary,
                        style = TodoAppTheme.typography.body
                    )
                },
                onClick = {
                    onOptionSelect(it)
                    expanded = false
                }
            )
        }
    }
}