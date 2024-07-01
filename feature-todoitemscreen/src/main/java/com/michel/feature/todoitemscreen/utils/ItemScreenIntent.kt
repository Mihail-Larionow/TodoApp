package com.michel.feature.todoitemscreen.utils

import com.michel.core.data.models.Importance

internal sealed interface ItemScreenIntent {
    data object GetItemInfoIntent : ItemScreenIntent
    data object DeleteIntent : ItemScreenIntent
    data object SaveIntent : ItemScreenIntent
    data object ToListScreenIntent : ItemScreenIntent
    data class SetTextIntent(val text: String) : ItemScreenIntent
    data class SetPriorityIntent(val importance: Importance) : ItemScreenIntent
    data class SetDeadlineDateIntent(val deadline: Long) : ItemScreenIntent
    data class SetDeadlineStateIntent(val hasDeadline: Boolean) : ItemScreenIntent
    data class SetDatePickerStateIntent(val isExpanded: Boolean) : ItemScreenIntent
    data class SetPriorityMenuStateIntent(val isExpanded: Boolean) : ItemScreenIntent
}