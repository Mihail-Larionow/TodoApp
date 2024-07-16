package com.michel.core.data.datasource.local

import com.michel.core.data.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    // Возвращает значение локальной ревизии
    fun getRevision(): Int

    // Устанавливает новое значение локальной ревизии
    fun setRevision(value: Int)

    // Возвращает флоу со списком тасок
    fun getAllItemsFlow(): Flow<List<TodoItem>>

    // Возвращает список тасок
    suspend fun getAllItems(): List<TodoItem>

    // Возвращает одну таску
    suspend fun getItem(itemId: String): TodoItem

    // Добавляет или изменяет таску
    suspend fun addOrUpdateItem(item: TodoItem)

    // Удаляет таску
    suspend fun deleteItem(item: TodoItem)

    // Очищает таблицу
    suspend fun deleteAllItems()

}