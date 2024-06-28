package com.michel.core.data.mappers

import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import com.michel.network.api.dto.PriorityDto
import com.michel.network.api.dto.TodoItemEntity

fun TodoItemEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        priority = this.priorityDto.toPriority(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun PriorityDto.toPriority(): Priority {
    return when (this) {
        PriorityDto.High -> Priority.High
        PriorityDto.Low -> Priority.Low
        PriorityDto.Standard -> Priority.Standard
    }
}

fun TodoItem.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        priorityDto = this.priority.toPriorityEntity(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun Priority.toPriorityEntity(): PriorityDto {
    return when (this) {
        Priority.High -> PriorityDto.High
        Priority.Low -> PriorityDto.Low
        Priority.Standard -> PriorityDto.Standard
    }
}