package com.michel.core.date

import com.michel.core.date.models.Priority
import com.michel.core.date.models.TodoItem

class TodoItemsRepository {

    fun getAll(): List<TodoItem> {
        return todoItems
    }

    fun add(todoItem: TodoItem): Boolean {
        todoItems.add(todoItem)
        return true
    }

    // Hardcoded data
    val todoItems = mutableListOf(
        TodoItem(
            id = "1",
            text = "some text",
            priority = Priority.Standard,
            isDone = false
        ),
        TodoItem(
            id = "2",
            text = "some text",
            priority = Priority.Standard,
            isDone = true
        ),
        TodoItem(
            id = "3",
            text = "some text",
            priority = Priority.High,
            isDone = false
        ),
        TodoItem(
            id = "4",
            text = "some text",
            priority = Priority.Low,
            isDone = false
        ),
        TodoItem(
            id = "5",
            text = "some text",
            priority = Priority.High,
            isDone = true
        ),
        TodoItem(
            id = "6",
            text = "some text",
            priority = Priority.Low,
            isDone = true
        )
    )
}