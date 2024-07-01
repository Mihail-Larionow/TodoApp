package com.michel.core.data.mappers

import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.network.api.dto.ImportanceDto
import com.michel.network.api.dto.TodoItemDto

fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        importance = this.importanceDto.toPriority(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun ImportanceDto.toPriority(): Importance {
    return when (this) {
        ImportanceDto.High -> Importance.High
        ImportanceDto.Low -> Importance.Low
        ImportanceDto.Standard -> Importance.Standard
    }
}

fun TodoItem.toTodoItemEntity(): TodoItemDto {
    return TodoItemDto(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        importanceDto = this.importance.toPriorityEntity(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun Importance.toPriorityEntity(): ImportanceDto {
    return when (this) {
        Importance.High -> ImportanceDto.High
        Importance.Low -> ImportanceDto.Low
        Importance.Standard -> ImportanceDto.Standard
    }
}