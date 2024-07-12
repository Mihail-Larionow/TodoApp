package com.michel.database

import com.michel.database.room.models.TodoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Database interface
 */
interface TodoItemsDatabase {

    // Возвращает значение ревизии из бд
    fun getRevision(): Int?

    // Устанавливает новое значение ревизии в бд
    fun setRevision(value: Int)

    // Возвращает флоу со списком тасок
    fun getAllItemsFlow(): Flow<List<TodoItemEntity>>

    // Возвращает список тасок
    suspend fun getAllItems(): List<TodoItemEntity>

    // Возвращает одну таску
    suspend fun getItem(entityId: String): TodoItemEntity

    // Добавляет или изменяет таску
    suspend fun upsertItem(entity: TodoItemEntity)

    // Удаляет таску
    suspend fun deleteItem(entity: TodoItemEntity)

    // Удаляет все таски
    suspend fun deleteAllItems()

}