package com.michel.core.data

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
            priority = Priority.STANDARD,
            isDone = false
        ),
        TodoItem(
            id = "2",
            text = "some text",
            priority = Priority.STANDARD,
            isDone = true
        ),
        TodoItem(
            id = "3",
            text = "some text",
            priority = Priority.HIGH,
            isDone = false
        ),
        TodoItem(
            id = "4",
            text = "some text",
            priority = Priority.LOW,
            isDone = false
        ),
        TodoItem(
            id = "5",
            text = "some text",
            priority = Priority.HIGH,
            isDone = true
        ),
        TodoItem(
            id = "6",
            text = "some text",
            priority = Priority.LOW,
            isDone = true
        )
    )
}