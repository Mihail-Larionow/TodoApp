package com.michel.network.api

import com.michel.network.dto.TodoItemDto

internal const val MAX_RETRIES_COUNT = 2
internal const val MAX_IDLE_CONNECTIONS_COUNT = 3
internal const val KEEP_ALIVE_DURATION_MINUTES = 5L
internal const val RETRY_INTERVAL = 3000L
internal const val CONNECTION_TIMEOUT = 10000L
internal const val READ_WRITE_TIMEOUT = 10000L

/**
 * Interface that provides methods to work with a server
 */
interface TodoItemsApi {

    // Возвращает значение ревизии c бекенда
    fun getRevision(): Int?

    // Получить все таски с бекенда
    suspend fun getAll(): List<TodoItemDto>

    // Получить одну таску по айди
    suspend fun getItem(id: String): TodoItemDto

    // Удалить таску
    suspend fun deleteItem(id: String): TodoItemDto

    // Обновить таску
    suspend fun updateItem(todoItem: TodoItemDto): TodoItemDto

    // Добавить таску
    suspend fun addItem(todoItem: TodoItemDto): TodoItemDto

    // Добавить таску
    suspend fun updateAll(todoItems: List<TodoItemDto>): List<TodoItemDto>

}