package com.michel.feature.screens.todolistscreen.utils

import com.michel.core.data.models.TodoItem

internal sealed class ListScreenEvent {
    data object GetItemsEvent: ListScreenEvent()
    data class DeleteItemEvent(val item: TodoItem): ListScreenEvent()
    data class ChangeVisibilityEvent(val isNotVisible: Boolean): ListScreenEvent()
    data class ToItemScreenEvent(val id: String): ListScreenEvent()
    data class UpdateItemEvent(val item: TodoItem): ListScreenEvent()
}