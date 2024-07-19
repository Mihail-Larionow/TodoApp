package com.michel.feature.todolistscreen.utils

import com.michel.core.data.models.TodoItem
import com.michel.core.ui.viewmodel.ScreenEffect

/**
 * Side effects types of items list screen
 */
internal sealed interface ListScreenEffect : ScreenEffect {
    data class LeaveScreenToItemEffect(val id: String) : ListScreenEffect
    data object LeaveScreenToSettingsEffect : ListScreenEffect
    data class ShowSimpleSnackBarEffect(val message: String) : ListScreenEffect
    data class ShowButtonSnackBarEffect(val message: String, val actionText: String) :
        ListScreenEffect
    data class ShowTimerSnackBarEffect(val item: TodoItem, val message: String, val actionText: String) :
        ListScreenEffect
}