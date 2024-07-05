package com.michel.network.api.dto

import java.util.Date

data class TodoItemDto(
    val id: String,
    val text: String,
    val importance: String,
    val deadline: Long? = null,
    val done: Boolean,
    val color: String? = null,
    val changed_at: Long? = null,
    val created_at: Long,
    val last_updated_by: String = "device-id"
)

fun emptyTodoItemDto(): TodoItemDto {
    return TodoItemDto(
        id = "",
        text = "",
        importance = "basic",
        done = false,
        created_at = Date().time
    )
}
