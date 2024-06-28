package com.michel.feature.screens.todoitemscreen.utils

import com.michel.core.data.models.Priority

internal sealed class ItemScreenIntent {
    data object GetItemInfoIntent : ItemScreenIntent()
    data object DeleteIntent : ItemScreenIntent()
    data object SaveIntent : ItemScreenIntent()
    data object ToListScreenIntent : ItemScreenIntent()
    data class SetTextIntent(val text: String) : ItemScreenIntent()
    data class SetPriorityIntent(val priority: Priority) : ItemScreenIntent()
    data class SetDeadlineDateIntent(val deadline: Long) : ItemScreenIntent()
    data class SetDeadlineStateIntent(val hasDeadline: Boolean) : ItemScreenIntent()
    data class SetDatePickerStateIntent(val isExpanded: Boolean) : ItemScreenIntent()
    data class SetPriorityMenuStateIntent(val isExpanded: Boolean) : ItemScreenIntent()
}