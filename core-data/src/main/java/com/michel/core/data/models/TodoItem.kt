package com.michel.core.data.models

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long? = null,
    val isDone: Boolean,
    val createdAt: Long,
    val changedAt: Long? = null
)

sealed class Importance(val text: String) {
    data object High: Importance("!! Высокий")
    data object Standard: Importance("Нет")
    data object Low: Importance("Низкий")
}

fun emptyTodoItem(): TodoItem {
    return TodoItem(
        id = "",
        text = "",
        importance = Importance.Standard,
        isDone = false,
        createdAt = Date().time
    )
}
