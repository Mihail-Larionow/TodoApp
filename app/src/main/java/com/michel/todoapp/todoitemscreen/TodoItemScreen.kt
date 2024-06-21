package com.michel.todoapp.todoitemscreen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.michel.core.date.models.Priority
import com.michel.core.ui.theme.TodoAppTheme
import com.michel.core.ui.utils.TodoDivider
import com.michel.core.ui.utils.TodoDatePicker
import com.michel.todoapp.extensions.toDateText

@Composable
internal fun TodoItemScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoItemViewModel = hiltViewModel(),
    navigate: () -> Unit
) {
    
    TodoAppTheme{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = TodoAppTheme.color.backPrimary
                )
        ) {

            var datePickerExpanded by remember { mutableStateOf(false) }

            val scrollState = rememberScrollState(0)

            var textField by remember {
                mutableStateOf(viewModel.screenState.text)
            }

            var priorityOption by remember {
                mutableStateOf(viewModel.screenState.priority)
            }

            var hasDeadline by remember {
                mutableStateOf(viewModel.screenState.hasDeadline)
            }

            var deadlineDate by remember {
                mutableLongStateOf(viewModel.screenState.deadline)
            }

            var deadlineDateText by remember {
                mutableStateOf(deadlineDate.toDateText())
            }

            Body(
                scrollState = scrollState,
                priorityOption = priorityOption,
                textField = textField,
                hasDeadline = hasDeadline,
                deadlineDateText = deadlineDateText,
                onTextChange = {
                    textField = it
                    viewModel.screenState.text = it
                },
                onPriorityChange = {
                    priorityOption = it
                    viewModel.screenState.priority = it
                },
                onDeadlineToggle = {
                    hasDeadline = it
                    viewModel.screenState.hasDeadline = it
                },
                onDeadlineClick = {
                    datePickerExpanded = !datePickerExpanded
                },
                onDelete = {
                    viewModel.delete()
                    navigate()
                }
            )

            Header(
                onCancel = {
                    navigate()
                },
                onAccept = {
                    if(textField != "") { // Проверка на пустой текст
                        viewModel.save()
                        navigate()
                    }
                },
                modifier = Modifier
                    .height(
                        height = TodoAppTheme.size.toolBar
                    )
                    .background(
                        color = TodoAppTheme.color.backPrimary
                    )
            )

            if(datePickerExpanded) TodoDatePicker(
                date = deadlineDate,
                onConfirm = {
                    datePickerExpanded = !datePickerExpanded
                    deadlineDate = it
                    viewModel.screenState.deadline = it
                    deadlineDateText = it.toDateText()
                },
                onDismiss = {
                    datePickerExpanded = !datePickerExpanded
                }
            )

        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 16.dp)
    ){

        Icon(
            painter = painterResource(
                id = com.michel.core.ui.R.drawable.ic_exit
            ),
            contentDescription = stringResource(
                id = com.michel.core.ui.R.string.cancelUpperCase
            ),
            tint = TodoAppTheme.color.primary,
            modifier = Modifier
                .size(
                    size = TodoAppTheme.size.standardIcon
                )
                .clickable { onCancel() }
                .align(
                    alignment = Alignment.CenterVertically
                )
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = { onAccept() },
            modifier = Modifier.align(
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                text = stringResource(
                    id = com.michel.core.ui.R.string.saveUpperCase
                ),
                color = TodoAppTheme.color.blue,
                style = TodoAppTheme.typography.button
            )
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    textField: String,
    priorityOption: Priority,
    hasDeadline: Boolean,
    deadlineDateText: String,
    onTextChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDeadlineToggle: (Boolean) -> Unit,
    onDeadlineClick: () -> Unit,
    onDelete: () -> Unit
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
            text = textField,
            shape = TodoAppTheme.shape.container,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 16.dp
                )
        ) {
            onTextChange(it)
        }

        TodoPriority(
            option = priorityOption,
            modifier = Modifier
                .padding(all = 16.dp)
        ) {
            onPriorityChange(it)
        }

        TodoDivider(
            modifier = Modifier
                .padding(all = 16.dp)
        )

        TodoDeadline(
            date = deadlineDateText,
            hasDeadline = hasDeadline,
            onToggleChecked = { onDeadlineToggle(it) },
            onDeadlineClick = onDeadlineClick,
            modifier = Modifier.padding(
                all = 16.dp
            )
        )

        TodoDivider()

        DeleteButton(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDelete() }
                .padding(
                    all = 16.dp
                )
        )
    }

}

@Composable
private fun TodoTextField(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = RectangleShape,
    onValueChanged: (String) -> Unit
) {

    OutlinedTextField(
        value = text,
        onValueChange = {
            onValueChanged(it)
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = TodoAppTheme.color.backSecondary,
            focusedContainerColor = TodoAppTheme.color.backSecondary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorContainerColor = TodoAppTheme.color.backSecondary,
            errorIndicatorColor = TodoAppTheme.color.red,
            focusedTextColor = TodoAppTheme.color.primary,
            unfocusedTextColor = TodoAppTheme.color.primary
        ),
        placeholder = {
            Text(
                text = stringResource(
                    id = com.michel.core.ui.R.string.placeholder
                ),
                style = TodoAppTheme.typography.body
            )
        },
        minLines = 5,
        textStyle = TodoAppTheme.typography.body,
        modifier = modifier
    )
}

@Composable
private fun TodoPriority(
    modifier: Modifier = Modifier,
    option: Priority,
    onOptionSelect: (Priority) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        Priority.Standard,
        Priority.Low,
        Priority.High
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            }
    ) {
        Text(
            text = stringResource(
                id = com.michel.core.ui.R.string.priority
            ),
            color = TodoAppTheme.color.primary,
            style = TodoAppTheme.typography.body
        )
        Spacer(
            modifier = Modifier.height(
                height = 4.dp
            )
        )
        Text(
            text = option.text,
            color = TodoAppTheme.color.tertiary,
            style = TodoAppTheme.typography.body
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
                            text = it.text,
                            color = if(it == Priority.High) {
                                TodoAppTheme.color.red
                            } else {
                                TodoAppTheme.color.primary
                            },
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

}

@Composable
private fun TodoDeadline(
    modifier: Modifier = Modifier,
    date: String,
    hasDeadline: Boolean,
    onToggleChecked: (Boolean) -> Unit,
    onDeadlineClick: () -> Unit
) {

    Row(
        modifier = modifier
    ){
        Column {
            Text(
                text = stringResource(
                    id = com.michel.core.ui.R.string.do_before
                ),
                color = TodoAppTheme.color.primary,
                style = TodoAppTheme.typography.body
            )

            if(hasDeadline) {
                Spacer(
                    modifier = Modifier.height(
                        height = 4.dp
                    )
                )
                Text(
                    text = date,
                    color = TodoAppTheme.color.blue,
                    style = TodoAppTheme.typography.button,
                    modifier = Modifier.clickable {
                        onDeadlineClick()
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = hasDeadline,
            onCheckedChange = { onToggleChecked(it) },
            colors = SwitchDefaults.colors(
                checkedIconColor = TodoAppTheme.color.blue,
                checkedThumbColor = TodoAppTheme.color.blue,
                checkedTrackColor = TodoAppTheme.color.blue.copy(
                    alpha = 0.5f
                ),
                checkedBorderColor = TodoAppTheme.color.blue,
                uncheckedIconColor = TodoAppTheme.color.elevated,
                uncheckedThumbColor = TodoAppTheme.color.elevated,
                uncheckedTrackColor = TodoAppTheme.color.overlay,
                uncheckedBorderColor = TodoAppTheme.color.overlay
            ),

        )
    }

}

@Composable
private fun DeleteButton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {

        Icon(
            painter = painterResource(
                id = com.michel.core.ui.R.drawable.ic_delete
            ),
            tint = TodoAppTheme.color.red,
            contentDescription = stringResource(
                id = com.michel.core.ui.R.string.deleteContentDescription
            ),
            modifier = Modifier
                .size(
                    size = TodoAppTheme.size.standardIcon
                )
                .align(
                    alignment = Alignment.CenterVertically
                )
        )

        Spacer(
            modifier = Modifier.width(
                width = 16.dp
            )
        )

        Text(
            text = stringResource(
                id = com.michel.core.ui.R.string.deleteUpperCase
            ),
            color = TodoAppTheme.color.red,
            style = TodoAppTheme.typography.body,
            modifier = Modifier.align(
                alignment = Alignment.CenterVertically
            )
        )

    }
}

