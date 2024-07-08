package com.michel.core.data.mappers

import com.michel.core.data.models.Importance
import com.michel.core.data.models.TodoItem
import com.michel.database.data.TodoItemEntity
import com.michel.network.api.dto.TodoItemDto

// Преобразовывает TodoItemDto в TodoItem
fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        isDone = this.done,
        importance = this.importance.toImportance(),
        deadline = this.deadline,
        createdAt = this.created_at,
        changedAt = this.changed_at
    )
}

// Преобразовывает TodoItemEntity в TodoItem
fun TodoItemEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        importance = this.importance.toImportance(),
        deadline = this.deadline,
        changedAt = this.changedAt,
        createdAt = this.createdAt
    )
}

// Преобразовывает TodoItem в TodoItemDto
fun TodoItem.toTodoItemDto(): TodoItemDto {
    return TodoItemDto(
        id = this.id,
        text = this.text,
        done = this.isDone,
        importance = this.importance.toImportanceEntity(),
        deadline = this.deadline,
        changed_at = this.changedAt,
        created_at = this.createdAt
    )
}

// Преобразовывает TodoItem в TodoItemEntity
fun TodoItem.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = this.id,
        text = this.text,
        isDone = this.isDone,
        importance = this.importance.toImportanceEntity(),
        deadline = this.deadline,
        changedAt = this.changedAt,
        createdAt = this.createdAt
    )
}

// Преобразовывает строковый importance в Importance
private fun String.toImportance(): Importance {
    return when (this) {
        "important" -> Importance.High
        "low" -> Importance.Low
        else -> Importance.Standard
    }
}

// Преобразовывает Importance в строковый importance
private fun Importance.toImportanceEntity(): String {
    return when (this) {
        Importance.High -> "important"
        Importance.Low -> "low"
        Importance.Standard -> "basic"
    }
}

