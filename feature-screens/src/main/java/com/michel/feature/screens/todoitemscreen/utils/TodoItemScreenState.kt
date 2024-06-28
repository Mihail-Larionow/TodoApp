package com.michel.feature.screens.todoitemscreen.utils

import com.michel.core.data.models.Priority

data class TodoItemScreenState(
    val text: String,
    val priority: Priority,
    val hasDeadline: Boolean,
    val deadline: Long,
    val priorityMenuExpanded: Boolean,
    val datePickerExpanded: Boolean,
    val loading: Boolean,
    val failed: Boolean,
    val errorMessage: String
)