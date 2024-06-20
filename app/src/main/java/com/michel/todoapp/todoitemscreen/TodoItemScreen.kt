package com.michel.todoapp.todoitemscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.michel.core.date.models.Priority
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.utils.SpecialDivider
import com.michel.core.ui.utils.TodoDatePicker

@Preview(showBackground = true)
@Composable
internal fun TodoItemScreen(
    todoItemViewModel: TodoItemViewModel = hiltViewModel(),
    id: String? = "none"
) {

    var datePickerExpanded by remember { mutableStateOf(false) }
    
    TodoAppTheme{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = TodoAppTheme.color.backPrimary
                )
        ) {
            val scrollState = rememberScrollState(0)
            Body(
                scrollState = scrollState,
                onDeadlineClick = {
                    datePickerExpanded = !datePickerExpanded
                }
            )
            Header(
                modifier = Modifier
                    .height(
                        height = TodoAppTheme.size.toolBar
                    )
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
            )

            if(datePickerExpanded) TodoDatePicker(
                onConfirm = {
                    datePickerExpanded = !datePickerExpanded
                },
                onDismiss = {
                    datePickerExpanded = !datePickerExpanded
                }
            )
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 16.dp)
    ){
        IconButton(
            onClick = { },
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterVertically
                )
        ) {
            Image(
                painter = painterResource(id = com.michel.core.ui.R.drawable.ic_exit),
                contentDescription = "exit",
                colorFilter = ColorFilter.tint(
                    color = TodoAppTheme.color.primary
                ),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                id = com.michel.core.ui.R.string.save
            ),
            color = TodoAppTheme.color.blue,
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterVertically
                )
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    onDeadlineClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Spacer(
            modifier = Modifier.height(
                height = TodoAppTheme.size.toolBar
            )
        )
        TodoTextField(
            shape = TodoAppTheme.shape.container,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 16.dp
                )
        )
        TodoPriority(modifier = Modifier
            .padding(all = 16.dp)
        )
        SpecialDivider(
            modifier = Modifier
                .padding(all = 16.dp)
        )
        TodoDeadline(
            onDeadlineClick = onDeadlineClick,
            modifier = Modifier
                .padding(all = 16.dp)
        )
        SpecialDivider()
        DeleteButton(
            modifier = Modifier
                .padding(all = 16.dp)
        )
    }

}

@Composable
private fun TodoTextField(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape
) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = TodoAppTheme.color.backSecondary,
            focusedContainerColor = TodoAppTheme.color.backSecondary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = stringResource(id = com.michel.core.ui.R.string.placeholder)
            )
        },
        minLines = 5,
        modifier = modifier
    )
}

@Composable
private fun TodoPriority(
    modifier: Modifier = Modifier,
    priority: Priority = Priority.Standard,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(priority) }
    val options = listOf(
        Priority.Standard,
        Priority.Low,
        Priority.High,
        )
    Column(
        modifier = modifier
            .clickable {
                expanded = !expanded
            }
    ) {
        Text("Важность")

        Text(
            text = selectedOption.text,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false })
        {
            options.forEach {
                option -> DropdownMenuItem(
                text = {
                       Text(option.text)
                },
                onClick = {
                    selectedOption = option
                    expanded = false
                }
                )
            }
        }
    }
}

@Composable
private fun TodoDeadline(
    modifier: Modifier = Modifier,
    onDeadlineClick: () -> Unit
) {
    var toggleChecked by remember { mutableStateOf(true) }
    
    Row(
        modifier = modifier
    ){
        Column(modifier = Modifier
            .align(
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(text = "Сделать до")
            if(toggleChecked) {
                Text(
                    text = "data",
                    modifier = Modifier.clickable {
                        onDeadlineClick()
                    }
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = toggleChecked,
            onCheckedChange = { toggleChecked = it },
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterVertically
                )
        )
    }

}

@Composable
private fun DeleteButton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
       IconButton(
           onClick = { },
           modifier = Modifier
               .align(
                   alignment = Alignment.CenterVertically
               )
       ) {
           Image(
               painter = painterResource(id = com.michel.core.ui.R.drawable.ic_delete),
               colorFilter = ColorFilter.tint(
                   color = TodoAppTheme.color.red
               ),
               contentDescription = "delete"
           )
       }
        Text(
            text = "УДАЛИТЬ",
            color = TodoAppTheme.color.red,
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterVertically
                )
        )
    }
}

