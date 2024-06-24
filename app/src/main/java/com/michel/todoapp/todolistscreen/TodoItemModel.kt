package com.michel.todoapp.todolistscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.michel.core.date.models.Priority
import com.michel.core.date.models.TodoItem
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.utils.ImageCheckBox
import com.michel.todoapp.extensions.toDateText

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TodoItemModel(
    todoItem: TodoItem,
    checked: Boolean,
    onCheckBoxClick: (Boolean) -> Unit,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit
) {

    Row(
       modifier = Modifier
           .fillMaxWidth()
           .combinedClickable(
               onClick = { onClick(todoItem.id) },
               onLongClick = { onLongClick(todoItem.id) }
           )
           .background(
               color = TodoAppTheme.color.backSecondary
           )
           .padding(16.dp)
    ) {

        TodoCheckbox(
            todoPriority = todoItem.priority,
            checked = checked,
            onCheckChanged = {
                onCheckBoxClick(it)
            },
            modifier = Modifier.size(
                size = TodoAppTheme.size.smallIcon
            )
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        if(!checked) {
            TodoItemPriority(
                priority = todoItem.priority,
                modifier = Modifier.size(
                    size = TodoAppTheme.size.smallIcon
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            if(checked) {
                Text(
                    text = todoItem.text,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TodoAppTheme.typography.body,
                    color = TodoAppTheme.color.tertiary,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else {
                Text(
                    text = todoItem.text,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TodoAppTheme.typography.body,
                    color = TodoAppTheme.color.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val deadline = todoItem.deadline
            if(deadline != null) {
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
            painter = painterResource(id = com.michel.core.ui.R.drawable.ic_info),
            contentDescription = "information",
            tint = TodoAppTheme.color.tertiary,
            modifier = Modifier.size(
                size = TodoAppTheme.size.smallIcon
            ),
        )

    }
}

@Composable
private fun TodoCheckbox(
    todoPriority: Priority,
    checked: Boolean,
    onCheckChanged: (state: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ImageCheckBox(
        checked = checked,
        onCheckedChange = {
            onCheckChanged(it)
        },
        iconChecked = R.drawable.ic_checked,
        iconUnchecked = if(todoPriority == Priority.High){
            R.drawable.ic_unchecked_high
        } else{
            R.drawable.ic_unchecked
        },
        modifier = modifier
    )
}

@Composable
private fun TodoItemPriority(
    modifier: Modifier = Modifier,
    priority: Priority
) {

    when(priority) {
        Priority.High -> {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_high_priority
                ),
                contentDescription = stringResource(
                    id = R.string.priorityIconContentDescription
                ),
                tint = TodoAppTheme.color.red,
                modifier = modifier
            )
            Spacer(
                modifier = Modifier.width(
                    width = 4.dp
                )
            )
        }
        Priority.Standard -> { }
        Priority.Low -> {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_low_priority
                ),
                contentDescription = stringResource(
                    id = R.string.priorityIconContentDescription
                ),
                tint = TodoAppTheme.color.gray,
                modifier = modifier
            )
            Spacer(
                modifier = Modifier.width(
                    width = 4.dp
                )
            )
        }
    }

}