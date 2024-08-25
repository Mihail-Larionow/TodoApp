package com.michel.network.service

import com.michel.network.R
import com.michel.network.dto.TodoItemDto
import com.michel.network.wrappers.ItemWrapper
import com.michel.network.wrappers.ListWrapper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface TodoItemsService {

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