package com.michel.core.date.models

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date? = null,
    val isDone: Boolean,
    val dateChanged: Date? = null
)

sealed class Priority(val text: String) {
    data object High: Priority("Высокий")
    data object Standard: Priority("Нет")
    data object Low: Priority("Низкий")

}
