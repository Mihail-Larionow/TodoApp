package com.michel.network.api.dto

import java.util.Date

data class TodoItemDto(
    val id: String,
    val text: String,
    val importanceDto: ImportanceDto,
    val deadline: Long? = null,
    val isDone: Boolean,
    val createdAt: Long,
    val changedAt: Long? = null
)

sealed class ImportanceDto {
    data object High : ImportanceDto()
    data object Standard : ImportanceDto()
    data object Low : ImportanceDto()
}

fun emptyTodoItemEntity(): TodoItemDto {
    return TodoItemDto(
        id = "",
        text = "",
        importanceDto = ImportanceDto.Standard,
        isDone = false,
        createdAt = Date().time
    )
}
