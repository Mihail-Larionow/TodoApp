package com.michel.core.data.models

import java.util.Date

/**
 *  Data class TodoItem
 */
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long? = null,
    val isDone: Boolean,
    val createdAt: Long,
    val changedAt: Long? = null
)

// Возвращает, так сказать, пустой TodoItem
fun emptyTodoItem(): TodoItem {
    return TodoItem(
        id = "",
        text = "",
        importance = Importance.Basic,
        isDone = false,
        createdAt = Date().time
    )
}
