package com.michel.network.api.dto

data class TodoItemsListDto(
    val list: List<TodoItemDto>,
    val status: String,
    val revision: Int
)