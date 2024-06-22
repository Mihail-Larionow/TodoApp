package com.michel.core.ui.utils

import androidx.compose.foundation.background
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.michel.core.ui.R
import com.michel.core.ui.theme.TodoAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDatePicker(
    modifier: Modifier = Modifier,
    date: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
){
    val datePickerState = rememberDatePickerState(date)

    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        datePickerState.selectedDateMillis ?: datePickerState.displayedMonthMillis
                    )
                }
            ) {
                Text(
                    text = stringResource(id = R.string.acceptUpperCase),
                    color = TodoAppTheme.color.blue,
                    style = TodoAppTheme.typography.button
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.cancelUpperCase),
                    color = TodoAppTheme.color.blue,
                    style = TodoAppTheme.typography.button
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = TodoAppTheme.color.backSecondary
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                todayContentColor = TodoAppTheme.color.primary,
                selectedDayContentColor = TodoAppTheme.color.elevated,
                selectedDayContainerColor = TodoAppTheme.color.blue,
                todayDateBorderColor = Color.Transparent,
                dayContentColor = TodoAppTheme.color.primary,
                titleContentColor = TodoAppTheme.color.primary,
                headlineContentColor = TodoAppTheme.color.primary,
                weekdayContentColor = TodoAppTheme.color.tertiary,
                navigationContentColor = TodoAppTheme.color.primary,
                yearContentColor = TodoAppTheme.color.primary,
                selectedYearContentColor = TodoAppTheme.color.elevated,
                selectedYearContainerColor = TodoAppTheme.color.blue,
                disabledSelectedYearContainerColor = TodoAppTheme.color.red
            )
        )
    }
}