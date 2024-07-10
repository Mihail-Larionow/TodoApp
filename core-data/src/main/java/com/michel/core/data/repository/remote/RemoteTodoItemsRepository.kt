package com.michel.core.data.repository.remote

import com.michel.core.data.models.TodoItem

interface RemoteTodoItemsRepository {

    // Возвращает значение удаленной ревизии
    fun getRevision(): Int?

    // Возвращает список тасок
    suspend fun getAll(): Result<List<TodoItem>>

    // Изменяет таску
    suspend fun updateItem(todoItem: TodoItem): Result<TodoItem>

    // Добавляет таску
    suspend fun addItem(todoItem: TodoItem): Result<TodoItem>

    // Удаляет таску
    suspend fun deleteItem(id: String): Result<TodoItem>

    // Возвращает таску
    suspend fun getItem(id: String): Result<TodoItem>

    // Мержит все таски
    suspend fun updateAll(todoItems: List<TodoItem>): Result<List<TodoItem>>

}