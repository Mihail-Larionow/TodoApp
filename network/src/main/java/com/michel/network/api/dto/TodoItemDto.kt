package com.michel.network.api.dto

/**
 * Data class TodoItem to work with a server
 */
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
