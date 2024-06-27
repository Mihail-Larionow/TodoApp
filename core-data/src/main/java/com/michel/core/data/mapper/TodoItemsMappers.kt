package com.michel.core.data.mapper

import com.michel.core.data.models.Priority
import com.michel.core.data.models.TodoItem
import com.michel.network.api.models.PriorityEntity
import com.michel.network.api.models.TodoItemEntity

fun TodoItemEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        priority = this.priorityEntity.toPriority(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun PriorityEntity.toPriority(): Priority {
    return when(this) {
        PriorityEntity.High -> Priority.High
        PriorityEntity.Low -> Priority.Low
        PriorityEntity.Standard -> Priority.Standard
    }
}

fun TodoItem.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        priorityEntity = this.priority.toPriorityEntity(),
        deadline = this.deadline,
        createdAt = this.createdAt,
        changedAt = this.changedAt
    )
}

private fun Priority.toPriorityEntity(): PriorityEntity {
    return when(this) {
        Priority.High -> PriorityEntity.High
        Priority.Low -> PriorityEntity.Low
        Priority.Standard -> PriorityEntity.Standard
    }
}