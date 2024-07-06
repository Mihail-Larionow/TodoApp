package com.michel.network.api.dto

/**
 * Data class that used in responses
 */
data class ListOfElementsResponse(
    val list: List<TodoItemDto>,
    val status: String,
    val revision: Int
)