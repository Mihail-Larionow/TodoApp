package com.michel.network.api.dto

import java.util.Date

data class TodoItemEntity(
    val id: String,
    var text: String,
    var priorityDto: PriorityDto,
    var deadline: Long? = null,
    var isDone: Boolean,
    val createdAt: Long,
    var changedAt: Long? = null
)

sealed class PriorityDto(val text: String) {
    data object High: PriorityDto("!! Высокий")
    data object Standard: PriorityDto("Нет")
    data object Low: PriorityDto("Низкий")
}

fun emptyTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = "",
        text = "",
        priorityDto = PriorityDto.Standard,
        isDone = false,
        createdAt = Date().time
    )
}
