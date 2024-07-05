package com.michel.core.data.repository

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemDto
import com.michel.core.data.models.TodoItem
import com.michel.network.api.backend.TodoItemsApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements work with remote data sources
 */
@Singleton
class RemoteTodoItemsRepository @Inject constructor(
    private val api: TodoItemsApi,
    tokenRepo: TokenRepository
) {

    init {
        val token = tokenRepo.getToken()
        token?.let {
            api.setToken(token)
        }
    }

    // Возвращает список тасок
    suspend fun getAll(): List<TodoItem> =
        api.getAll().map { it.toTodoItem() }

    // Изменяет таску
    suspend fun updateItem(todoItem: TodoItem): TodoItem =
        api.updateItem(todoItem.toTodoItemDto()).toTodoItem()

    // Добавляет таску
    suspend fun addItem(todoItem: TodoItem): TodoItem =
        api.addItem(todoItem.toTodoItemDto()).toTodoItem()

    // Удаляет таску
    suspend fun deleteItem(id: String): TodoItem =
        api.deleteItem(id).toTodoItem()

    // Возвращает таску
    suspend fun getItem(id: String): TodoItem =
        api.getItem(id).toTodoItem()

    // Мержит все таски
    suspend fun updateAll(todoItems: List<TodoItem>): List<TodoItem> =
        api.updateAll(todoItems.map { it.toTodoItemDto() }).map { it.toTodoItem() }

}