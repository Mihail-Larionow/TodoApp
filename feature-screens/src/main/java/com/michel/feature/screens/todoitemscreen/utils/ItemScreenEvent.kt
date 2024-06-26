package com.michel.feature.screens.todoitemscreen.utils

import com.michel.core.data.models.Priority

internal sealed class ItemScreenEvent {
    data object GetItemInfoEvent: ItemScreenEvent()
    data object DeleteEvent : ItemScreenEvent()
    data object SaveEvent : ItemScreenEvent()
    data object ToListScreenEvent : ItemScreenEvent()
    data class SetTextEvent(val text: String): ItemScreenEvent()
    data class SetPriorityEvent(val priority: Priority): ItemScreenEvent()
    data class SetDeadlineDateEvent(val deadline: Long): ItemScreenEvent()
    data class SetDeadlineState(val hasDeadline: Boolean): ItemScreenEvent()
    data class SetDatePickerState(val isExpanded: Boolean): ItemScreenEvent()
    data class SetPriorityMenuState(val isExpanded: Boolean): ItemScreenEvent()
}