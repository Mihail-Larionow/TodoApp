package com.michel.feature.todoitemscreen.utils

import com.michel.core.data.models.Importance
import com.michel.core.ui.extensions.toDateText
import com.michel.core.ui.viewmodel.ScreenState
import java.util.Date

/**
 * Contains state of item screen
 */
data class ItemScreenState(
    val text: String = "",
    val importance: Importance = Importance.Basic,
    val hasDeadline: Boolean = false,
    val deadline: Long = Date().time,
    val deadlineDateText: String = deadline.toDateText(),
    val priorityMenuExpanded: Boolean = false,
    val datePickerExpanded: Boolean = false,
    val deleteButtonEnabled: Boolean = false,
    val loading: Boolean = false,
    val failed: Boolean = false,
    val enabled: Boolean = true,
    val errorMessage: String = "",
) : ScreenState