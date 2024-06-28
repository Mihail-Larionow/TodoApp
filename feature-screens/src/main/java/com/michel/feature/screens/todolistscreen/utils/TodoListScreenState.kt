package com.michel.feature.screens.todolistscreen.utils

import com.michel.core.data.models.TodoItem

data class TodoListScreenState(
    val todoItems: List<TodoItem>,
    val doneItemsHide: Boolean,
    val doneItemsCount: Int,
    val failed: Boolean,
    val loading: Boolean,
    val errorMessage: String
)