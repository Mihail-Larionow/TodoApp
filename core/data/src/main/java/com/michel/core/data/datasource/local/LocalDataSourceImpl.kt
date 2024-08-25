package com.michel.core.data.datasource.local

import com.michel.core.data.mappers.toTodoItem
import com.michel.core.data.mappers.toTodoItemEntity
import com.michel.core.data.models.TodoItem
import com.michel.database.dao.RevisionDao
import com.michel.database.dao.TodoItemsDao
import com.michel.database.models.RevisionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements work with local data sources
 */
internal class LocalDataSourceImpl @Inject constructor(
    private val revisionDao: RevisionDao,
    private val todoItemsDao: TodoItemsDao,
) : LocalDataSource {

    // Возвращает значение локальной ревизии
    override fun getRevision(): Int {
        return revisionDao.get() ?: 0
    }

    // Устанавливает новое значение локальной ревизии
    override fun setRevision(value: Int) {
        revisionDao.set(RevisionEntity(revision = value))
    }

    // Возвращает флоу со списком тасок
    override fun getAllItemsFlow(): Flow<List<TodoItem>> {
        return todoItemsDao.getAllItemsFlow().map { list ->
            list.map { it.toTodoItem() }
        }
    }

    // Возвращает список тасок
    override suspend fun getAllItems(): List<TodoItem> {
        return todoItemsDao.getAllItems().map { it.toTodoItem() }
    }

    // Возвращает одну таску
    override suspend fun getItem(itemId: String): TodoItem {
        return todoItemsDao.getItem(itemId).toTodoItem()
    }

    // Добавляет или изменяет таску
    override suspend fun addOrUpdateItem(item: TodoItem) {
        todoItemsDao.upsertItem(item.toTodoItemEntity())
    }

    // Удаляет таску
    override suspend fun deleteItem(item: TodoItem) {
        todoItemsDao.deleteItem(item.toTodoItemEntity())
    }

    // Очищает таблицу
    override suspend fun deleteAllItems() {
        todoItemsDao.deleteAllItems()
    }

}