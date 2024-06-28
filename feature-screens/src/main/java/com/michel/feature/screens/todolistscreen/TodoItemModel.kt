package com.michel.feature.screens.todolistscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.R
import com.michel.core.ui.custom.ImageCheckbox
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.screens.extensions.toDateText
import com.michel.feature.screens.todolistscreen.utils.ListScreenIntent

@Composable
internal fun TodoItemModel(
    todoItem: TodoItem,
    checked: Boolean,
    onEvent: (ListScreenIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    Log.i("app", todoItem.id)
                    onEvent(ListScreenIntent.ToItemScreenIntent(todoItem.id))
                },
            )
            .background(
                color = TodoAppTheme.color.backSecondary
            )
            .padding(
                all = 16.dp
            )
    ) {
        TodoCheckbox(
            todoPriority = todoItem.priority,
            checked = checked,
            onCheckChanged = {
                onEvent(ListScreenIntent.UpdateItemIntent(todoItem.copy(isDone = it)))
            },
            modifier = Modifier.size(TodoAppTheme.size.smallIcon)
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )

        if (!checked) {
            TodoPriority(
                priority = todoItem.priority,
                modifier = Modifier.size(
                    size = TodoAppTheme.size.smallIcon
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            val textColor = if (checked) {
                TodoAppTheme.color.tertiary
            } else {
                TodoAppTheme.color.primary
            }

            val textDecoration = if (checked) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
            Text(
                text = todoItem.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = TodoAppTheme.typography.body,
                color = textColor,
                textDecoration = textDecoration,
                modifier = Modifier.fillMaxWidth()
            )

            val deadline = todoItem.deadline

            if (deadline != null) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = deadline.toDateText(),
                    style = TodoAppTheme.typography.subhead,
                    color = TodoAppTheme.color.tertiary
                )
            }
        }
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = stringResource(R.string.infoIconContentDescription),
            tint = TodoAppTheme.color.tertiary,
            modifier = Modifier.size(TodoAppTheme.size.smallIcon),
        )
    }
}

@Composable
private fun TodoCheckbox(
    todoPriority: Priority,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val checkedIcon = painterResource(R.drawable.ic_checked)
    val uncheckedIcon = if (todoPriority == Priority.High) {
        painterResource(R.drawable.ic_unchecked_high)
    } else {
        painterResource(R.drawable.ic_unchecked)
    }

    ImageCheckbox(
        checked = checked,
        onCheckedChange = { onCheckChanged(it) },
        checkedIcon = checkedIcon,
        uncheckedIcon = uncheckedIcon,
        modifier = modifier
    )
}

@Composable
private fun TodoPriority(
    modifier: Modifier = Modifier,
    priority: Priority
) {
    when (priority) {
        Priority.High -> {
            Icon(
                painter = painterResource(R.drawable.ic_high_priority),
                contentDescription = stringResource(R.string.priorityIconContentDescription),
                tint = TodoAppTheme.color.red,
                modifier = modifier
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
        }

        Priority.Standard -> {}
        Priority.Low -> {
            Icon(
                painter = painterResource(R.drawable.ic_low_priority),
                contentDescription = stringResource(R.string.priorityIconContentDescription),
                tint = TodoAppTheme.color.gray,
                modifier = modifier
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoItemModelPreview() {
    val checked = true
    val todoItem = TodoItem(
        id = "1",
        text = "short text but great idea",
        priority = Priority.Standard,
        isDone = true,
        deadline = 1718919593456,
        createdAt = 1718919593456,
        changedAt = 1718919593456
    )

    TodoItemModel(
        todoItem = todoItem,
        checked = checked,
        onEvent = { }
    )
}