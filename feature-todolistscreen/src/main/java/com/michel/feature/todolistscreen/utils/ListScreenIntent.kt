package com.michel.feature.todolistscreen.utils

import com.michel.core.data.models.TodoItem

internal sealed interface ListScreenIntent {
    data object GetItemsIntent : ListScreenIntent
    data class DeleteItemIntent(val item: TodoItem) : ListScreenIntent
    data class ChangeVisibilityIntent(val isNotVisible: Boolean) : ListScreenIntent
    data class ToItemScreenIntent(val id: String) : ListScreenIntent
    data class UpdateItemIntent(val item: TodoItem) : ListScreenIntent
}