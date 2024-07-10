package com.michel.core.data.repository.local

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemEntity
import com.michel.core.data.models.TodoItem
import com.michel.database.TodoItemsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Implements work with local data sources
 */
@Singleton
class LocalTodoItemsRepositoryImpl(private val db: TodoItemsDatabase) : LocalTodoItemsRepository {

    // Возвращает значение локальной ревизии
    override fun getRevision(): Int {
        return db.getRevision() ?: 0
    }

    // Устанавливает новое значение локальной ревизии
    override fun setRevision(revision: Int) {
        db.setRevision(revision)
    }

    // Возвращает флоу со списком тасок
    override fun getAllItemsFlow(): Flow<List<TodoItem>> {
        return db.getAllItemsFlow().map { list ->
            list.map { it.toTodoItem() }
        }
    }

    // Возвращает список тасок
    override suspend fun getAllItems(): List<TodoItem> {
        return db.getAllItems().map { it.toTodoItem() }
    }

    // Возвращает одну таску
    override suspend fun getItem(itemId: String): TodoItem {
        return db.getItem(itemId).toTodoItem()
    }

    // Добавляет или изменяет таску
    override suspend fun addOrUpdateItem(item: TodoItem) {
        db.upsertItem(item.toTodoItemEntity())
    }

    // Удаляет таску
    override suspend fun deleteItem(item: TodoItem) {
        db.deleteItem(item.toTodoItemEntity())
    }

    // Очищает таблицу
    override suspend fun deleteAllItems() {
        db.deleteAllItems()
    }

}