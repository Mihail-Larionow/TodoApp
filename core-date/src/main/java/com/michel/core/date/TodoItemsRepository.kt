package com.michel.core.date

import android.util.Log
import com.michel.core.date.models.Priority
import com.michel.core.date.models.TodoItem
import com.michel.core.date.models.emptyTodoItem
import java.util.Date

class TodoItemsRepository {

    // Возвращает все таски списком
    fun getAll(): List<TodoItem> {
        return todoItems
    }

    // Добавляет новую таску в список, либо заменяет таску с таким же id
    fun addItem(todoItem: TodoItem): Boolean {
        val item = todoItems.find { todoItem.id == it.id }
        if(item != null) {
            item.text = todoItem.text
            item.priority = todoItem.priority
            item.isDone = todoItem.isDone
            item.deadline = todoItem.deadline
            item.dateChanged = todoItem.dateChanged
        }
        else todoItems.add(todoItem)
        return true
    }

    // Удаляет таску из списка
    fun deleteItem(todoItem: TodoItem) {
        todoItems.remove(todoItem)
    }

    // Находит таску по id, либо возвращает пустую таску
    fun getItem(id: String): TodoItem {
        return todoItems.find {
            it.id == id
        } ?: emptyTodoItem()
    }

    // Хардкод дата
    private val todoItems = mutableListOf(
        TodoItem(
            id = "1",
            text = "Мега пж",
            priority = Priority.High,
            isDone = false,
            deadline = 1718919593456 + 86400000
        ),
        TodoItem(
            id = "2",
            text = "Поставьте максимум прошу",
            priority = Priority.High,
            isDone = false,
            deadline = 1718919593456 + 86400000
        ),
        TodoItem(
            id = "3",
            text = "Исправить все баги",
            priority = Priority.High,
            isDone = false,
            deadline = 1718919593456 + 86400000
        ),
        TodoItem(
            id = "4",
            text = "Тысячу раз задебажить это приложение",
            priority = Priority.Low,
            isDone = true,
            deadline = 1718919593456
        ),
        TodoItem(
            id = "5",
            text = "Сделать первое задание",
            priority = Priority.High,
            isDone = true
        ),
        TodoItem(
            id = "6",
            text = "Устроиться работать в пятерочку(",
            priority = Priority.Low,
            isDone = false
        ),
        TodoItem(
            id = "7",
            text = "Устроиться работать в Яндикс)",
            priority = Priority.High,
            isDone = false
        ),
        TodoItem(
            id = "8",
            text = "Выполненное задание",
            priority = Priority.Low,
            isDone = true
        ),
        TodoItem(
            id = "9",
            text = "Что-то важное",
            priority = Priority.High,
            isDone = false
        ),
        TodoItem(
            id = "10",
            text = "Что-то неважное",
            priority = Priority.Low,
            isDone = false
        ),
        TodoItem(
            id = "11",
            text = "Задание с дедлайном",
            priority = Priority.Standard,
            isDone = false,
            deadline = Date().time
        ),
        TodoItem(
            id = "12",
            text = "Очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень длинный текст",
            priority = Priority.Standard,
            isDone = false
        ),
        TodoItem(
            id = "13",
            text = "Вставьте текст",
            priority = Priority.Standard,
            isDone = false
        )
    )
}