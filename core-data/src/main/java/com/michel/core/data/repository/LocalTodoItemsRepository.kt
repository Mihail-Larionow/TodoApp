package com.michel.core.data.repository

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemEntity
import com.michel.core.data.models.TodoItem
import com.michel.database.data.TodoItemsDatabase
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Implements work with local data sources
 */
@Singleton
class LocalTodoItemsRepository(private val db: TodoItemsDatabase) {

    // Возвращает флоу со списком тасок
    val todoItems = db.todoItemDao.getAllItems().map { list ->
        list.map {
            it.toTodoItem()
        }
    }

    suspend fun getItem(itemId: String): TodoItem {
        return db.todoItemDao.getItem(itemId).toTodoItem()
    }

    // Добавляет или изменяет таску
    suspend fun addOrUpdate(item: TodoItem) {
        db.todoItemDao.upsertItem(item.toTodoItemEntity())
    }

    // Удаляет таску
    suspend fun deleteItem(item: TodoItem) {
        db.todoItemDao.deleteItem(item.toTodoItemEntity())
    }

    // Очищает таблицу
    suspend fun deleteAll() {
        db.todoItemDao.deleteAllItems()
    }

}