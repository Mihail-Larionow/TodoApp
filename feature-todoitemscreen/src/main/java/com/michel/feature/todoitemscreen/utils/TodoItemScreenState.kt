package com.michel.feature.todoitemscreen.utils

import com.michel.core.data.models.Importance

data class TodoItemScreenState(
    val text: String,
    val importance: Importance,
    val hasDeadline: Boolean,
    val deadline: Long,
    val priorityMenuExpanded: Boolean,
    val datePickerExpanded: Boolean,
    val loading: Boolean,
    val failed: Boolean,
    val errorMessage: String
)