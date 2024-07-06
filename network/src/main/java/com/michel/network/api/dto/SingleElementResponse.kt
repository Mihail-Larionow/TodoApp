package com.michel.network.api.dto

/**
 * Data class that used in responses
 */
data class SingleElementResponse(
    val element: TodoItemDto,
    val revision: Int,
    val status: String
)
