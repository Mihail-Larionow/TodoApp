package com.michel.todoapp.todolistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.michel.core.date.models.TodoItem
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.utils.ImageCheckBox
import com.michel.core.ui.utils.SwipeItem
import com.michel.todoapp.navigation.Screen

private val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
private val EXPANDED_TOP_BAR_HEIGHT = 200.dp

@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel(),
    onItemClick: (String) -> Unit,
) {

    TodoAppTheme{
        val listState = rememberLazyListState()

        var hideDoneItems by remember {
            mutableStateOf(viewModel.screenState.hideDoneItems)
        }

        var doneItemsCount by remember {
            mutableIntStateOf(
                viewModel.screenState.todoItems.count {
                    it.isDone
                }
            )
        }

        val isCollapsed: Boolean by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = TodoAppTheme.color.backPrimary
                )
        ) {
            Scaffold(

                topBar = {
                    CollapsedToolBar(
                        count = doneItemsCount,
                        isCollapsed = isCollapsed,
                        hideDoneItems = hideDoneItems,
                        onCheckChange = {
                            hideDoneItems = it
                            viewModel.screenState.hideDoneItems = it
                        },
                        modifier = Modifier.background(
                            color = TodoAppTheme.color.backPrimary
                        )
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Body(
                    state = listState,
                    itemList = viewModel.screenState.todoItems,
                    hideDoneItems = hideDoneItems,
                    doneItemsCount = doneItemsCount,
                    onVisibilityChanged = {
                        hideDoneItems = it
                        viewModel.screenState.hideDoneItems = it
                    },
                    onDelete = {
                        viewModel.deleteItem(item = it)
                    },
                    onItemClick = onItemClick,
                    onItemLongClick = {

                    },
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
                        (viewModel.screenState.todoItems.size + 1).toString()
                    )
                },
                modifier = Modifier.align(
                    Alignment.BottomEnd
                )
            )
        }
    }
}

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
        itemList.toMutableStateList()
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            state = state,
            modifier = modifier.background(
                color = TodoAppTheme.color.backSecondary
            )
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
                    text = stringResource(
                        id = com.michel.core.ui.R.string.new_task
                    ),
                    color = TodoAppTheme.color.tertiary,
                    style = TodoAppTheme.typography.body,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(
                                "${itemList.size + 1}"
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
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedToolBar(
    modifier: Modifier = Modifier,
    count: Int = 0,
    hideDoneItems: Boolean = false,
    onCheckChange: (check: Boolean) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                height = EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT
            )
            .padding(
                start = 64.dp,
                end = 24.dp,
                bottom = 16.dp
            )
    ) {
        Column(
            modifier = Modifier.align(
                alignment = Alignment.BottomStart
            )
        ) {
            Text(
                text = stringResource(
                    id = com.michel.core.ui.R.string.todolist_screen_title
                ),
                style = TodoAppTheme.typography.largeTitle,
                color = TodoAppTheme.color.primary,
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            AnimatedVisibility(
                visible = true,
                exit = shrinkOut(shrinkTowards = Alignment.TopStart) + fadeOut()
            ) {
                Text(
                    text = stringResource(
                        id = com.michel.core.ui.R.string.todolist_screen_subtitle
                    ) + " $count",
                    style = TodoAppTheme.typography.subhead,
                    color = TodoAppTheme.color.tertiary,
                )
            }
        }
        VisibilityCheckBox(
            checked = hideDoneItems,
            onCheckChange = { onCheckChange(it) },
            modifier = Modifier
                .size(
                    size = TodoAppTheme.size.standardIcon
                )
                .align(
                    alignment = Alignment.BottomEnd
                )
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsedToolBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = true,
    count: Int = 0,
    hideDoneItems: Boolean = false,
    onCheckChange: (check: Boolean) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isCollapsed,
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
            enter = expandVertically(expandFrom = Alignment.Bottom)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = com.michel.core.ui.R.string.todolist_screen_title
                    ),
                    style = TodoAppTheme.typography.title,
                    color = TodoAppTheme.color.primary,
                    modifier = Modifier.align(
                        alignment = Alignment.CenterStart
                    )
                )
                VisibilityCheckBox(
                    checked = hideDoneItems,
                    onCheckChange = { onCheckChange(it) },
                    modifier = Modifier
                        .size(
                            size = TodoAppTheme.size.standardIcon
                        )
                        .align(
                            alignment = Alignment.CenterEnd
                        )
                )
            }
        }
    }
}

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
                modifier = modifier
            ) {
                Icon(
                    painter = painterResource(
                        id = com.michel.core.ui.R.drawable.ic_add
                    ),
                    contentDescription = stringResource(
                        id = com.michel.core.ui.R.string.floatingButtonContentDescription
                    ),
                    modifier = Modifier.size(
                        size = TodoAppTheme.size.standardIcon
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VisibilityCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {

    ImageCheckBox(
        checked = checked,
        iconChecked = com.michel.core.ui.R.drawable.ic_visibility_off,
        iconUnchecked = com.michel.core.ui.R.drawable.ic_visibility_on,
        onCheckedChange = { onCheckChange(it) },
        modifier = modifier
    )

}

@Composable
private fun ItemDropdownMenu(
    modifier: Modifier = Modifier,
    onOptionSelect: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        stringResource(
            id = com.michel.core.ui.R.string.delete
        ),
        stringResource(
            id = com.michel.core.ui.R.string.done
        )
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