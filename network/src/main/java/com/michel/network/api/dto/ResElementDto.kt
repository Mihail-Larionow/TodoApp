package com.michel.network.api.dto

data class ResElementDto(
    val element: TodoItemDto,
    val revision: Int,
    val status: String
)
