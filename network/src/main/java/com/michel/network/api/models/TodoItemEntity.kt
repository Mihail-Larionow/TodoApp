package com.michel.network.api.models

import java.util.Date

data class TodoItemEntity(
    val id: String,
    var text: String,
    var priorityEntity: PriorityEntity,
    var deadline: Long? = null,
    var isDone: Boolean,
    val createdAt: Long,
    var changedAt: Long? = null
)

sealed class PriorityEntity(val text: String) {
    data object High: PriorityEntity("!! Высокий")
    data object Standard: PriorityEntity("Нет")
    data object Low: PriorityEntity("Низкий")
}

fun emptyTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = "",
        text = "",
        priorityEntity = PriorityEntity.Standard,
        isDone = false,
        createdAt = Date().time
    )
}
