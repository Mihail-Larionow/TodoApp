package com.michel.feature.todolistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.core.ui.R
import com.michel.core.ui.custom.ImageCheckbox
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.feature.todolistscreen.utils.ListScreenIntent
import java.util.Date

/**
 * UI model of TodoItem
 */
@Composable
internal fun TodoItemModel(
    todoItem: TodoItem,
    checked: Boolean,
    enabled: Boolean,
    onEvent: (ListScreenIntent) -> Unit
) {
    val backgroundColor = animateColorAsState(
        targetValue = if (enabled) {
            TodoAppTheme.color.backSecondary
        } else {
            TodoAppTheme.color.disable
        }, label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = { onEvent(ListScreenIntent.ToItemScreenIntent(todoItem.id)) },
            )
            .background(color = backgroundColor.value)
            .padding(all = 16.dp)
    ) {
        TodoCheckbox(
            todoImportance = todoItem.importance,
            checked = checked,
            enabled = enabled,
            onCheckChanged = {
                onEvent(
                    ListScreenIntent.UpdateItemIntent(
                        todoItem.copy(
                            isDone = it,
                            changedAt = Date().time
                        )
                    )
                )
            },
            modifier = Modifier.size(TodoAppTheme.size.standardIcon)
        )
        Spacer(modifier = Modifier.width(12.dp))
        AnimatedImportance(
            visible = !checked,
            importance = todoItem.importance,
            modifier = Modifier.size(size = TodoAppTheme.size.smallIcon)
        )
        Spacer(modifier = Modifier.width(4.dp))
        TodoText(
            todoItem = todoItem,
            checked = checked,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = stringResource(R.string.infoIconContentDescription),
            tint = TodoAppTheme.color.tertiary,
            modifier = Modifier.size(TodoAppTheme.size.standardIcon),
        )
    }
}

@Composable
private fun TodoText(
    modifier: Modifier = Modifier,
    todoItem: TodoItem,
    checked: Boolean,
    enabled: Boolean
) {
    Column(
        modifier = modifier
    ) {
        val textColor = animateColorAsState(
            targetValue = if (checked || !enabled) {
                TodoAppTheme.color.tertiary
            } else {
                TodoAppTheme.color.primary
            }, label = ""
        )

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
            color = textColor.value,
            textDecoration = textDecoration,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        AnimatedDeadline(
            visible = todoItem.deadline != null,
            deadline = todoItem.deadline.toDateText()
        )
    }
}

@Composable
private fun AnimatedDeadline(
    visible: Boolean,
    deadline: String
) {
    AnimatedVisibility(visible = visible) {
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Text(
            text = deadline,
            style = TodoAppTheme.typography.subhead,
            color = TodoAppTheme.color.tertiary
        )
    }
}

@Composable
private fun TodoCheckbox(
    todoImportance: Importance,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val checkedIcon = painterResource(R.drawable.ic_checked)
    val uncheckedIcon = painterResource(R.drawable.ic_unchecked)


    val unCheckedTint = if (todoImportance == Importance.High) {
        TodoAppTheme.color.red
    } else {
        TodoAppTheme.color.tertiary
    }

    ImageCheckbox(
        checked = checked,
        onCheckedChange = { onCheckChanged(it) },
        checkedIcon = checkedIcon,
        checkedTint = TodoAppTheme.color.green,
        uncheckedIcon = uncheckedIcon,
        uncheckedTint = unCheckedTint,
        disabledTint = TodoAppTheme.color.disable,
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
private fun AnimatedImportance(
    modifier: Modifier = Modifier,
    visible: Boolean,
    importance: Importance
) {
    AnimatedVisibility(visible = visible) {
        when (importance) {
            Importance.High -> HighImportance(modifier = modifier)
            Importance.Basic -> {}
            Importance.Low -> LowImportance(modifier = modifier)
        }
    }
}

@Composable
private fun HighImportance(
    modifier: Modifier = Modifier
) {
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

@Composable
private fun LowImportance(
    modifier: Modifier = Modifier
) {
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

@Preview(showBackground = true)
@Composable
private fun TodoItemModelPreview() {
    val checked = false
    val enabled = true
    val todoItem = TodoItem(
        id = "1",
        text = "short text but great idea",
        importance = Importance.High,
        isDone = checked,
        deadline = 1718919593456,
        createdAt = 1718919593456,
        changedAt = 1718919593456
    )

    TodoAppTheme {
        TodoItemModel(
            todoItem = todoItem,
            checked = checked,
            enabled = enabled,
            onEvent = { }
        )
    }
}