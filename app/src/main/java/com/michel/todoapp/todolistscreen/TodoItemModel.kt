package com.michel.todoapp.todolistscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.michel.core.date.models.Priority
import com.michel.core.date.models.TodoItem
import com.michel.core.ui.R
import com.michel.core.ui.utils.ImageCheckBox

@Composable
internal fun TodoItemModel(
    todoItem: TodoItem,
    onClick: (id: String) -> Unit
) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                onClick(todoItem.id)
            }
    ) {
        TodoCheckbox(
            todoPriority = todoItem.priority,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = todoItem.text,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(16.dp))
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = com.michel.core.ui.R.drawable.ic_info),
            contentDescription = "information"
        )

    }
}

@Composable
private fun TodoCheckbox(
    todoPriority: Priority,
    modifier: Modifier = Modifier
) {
    var checked: Boolean by remember { mutableStateOf(false) }
    if(todoPriority == Priority.High){
        ImageCheckBox(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            iconChecked = R.drawable.ic_checked,
            iconUnchecked = R.drawable.ic_unchecked_high,
            modifier = modifier
        )
    }
    else {
        ImageCheckBox(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            iconChecked = R.drawable.ic_checked,
            iconUnchecked = R.drawable.ic_unchecked,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemModel() {
    val todoItem = TodoItem(
        id = "1",
        text = "some text",
        priority = Priority.Standard,
        isDone = false
    )

    TodoItemModel(todoItem = todoItem){

    }
}
