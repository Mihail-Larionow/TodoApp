package com.michel.feature.todolistscreen.utils

import com.michel.core.data.models.TodoItem
import com.michel.core.ui.viewmodel.ScreenState

/**
 * Contains state of items list screen.
 */
data class ListScreenState(
    val todoItems: List<TodoItem> = emptyList(),
    val doneItemsHide: Boolean = false,
    val doneItemsCount: Int = 0,
    val failed: Boolean = false,
    val isRefreshing: Boolean = true,
    val errorMessage: String = "",
    val enabled: Boolean = true,
    val isConnected: Boolean = false,
) : ScreenState