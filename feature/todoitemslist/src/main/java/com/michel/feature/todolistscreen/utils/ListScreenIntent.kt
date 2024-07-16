package com.michel.feature.todolistscreen.utils

import com.michel.core.data.models.TodoItem
import com.michel.core.ui.viewmodel.ScreenIntent

/**
 * Intent types of items list screen
 */
internal sealed interface ListScreenIntent : ScreenIntent {
    data object GetItemsIntent : ListScreenIntent
    data object StartLoadingIntent : ListScreenIntent
    data class DeleteItemIntent(val item: TodoItem) : ListScreenIntent
    data class ChangeVisibilityIntent(val isNotVisible: Boolean) : ListScreenIntent
    data class ToItemScreenIntent(val id: String) : ListScreenIntent
    data object ToSettingsScreenIntent : ListScreenIntent
    data class UpdateItemIntent(val item: TodoItem) : ListScreenIntent
    data class UpdateItemsIntent(val items: List<TodoItem>) : ListScreenIntent
}