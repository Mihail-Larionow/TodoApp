package com.michel.database

import android.content.Context
import com.michel.database.room.RoomTodoItemsDatabase
import com.michel.database.room.models.RevisionEntity
import com.michel.database.room.models.TodoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implements work with database
 */
class TodoItemsDatabaseImpl(context: Context) : TodoItemsDatabase {

    private val todoItemsDao = RoomTodoItemsDatabase.getInstance(context).todoItemsDao
    private val revisionDao = RoomTodoItemsDatabase.getInstance(context).revisionDao

    // Возвращает значение ревизии из бд
    override fun getRevision(): Int? {
        return revisionDao.get()
    }

    // Устанавливает новое значение ревизии в бд
    override fun setRevision(value: Int) {
        revisionDao.set(RevisionEntity(revision = value))
    }

    // Возвращает флоу со списком тасок
    override fun getAllItemsFlow(): Flow<List<TodoItemEntity>> {
        return todoItemsDao.getAllItemsFlow()
    }

    // Возвращает список тасок
    override suspend fun getAllItems(): List<TodoItemEntity> {
        return todoItemsDao.getAllItems()
    }

    // Возвращает одну таску
    override suspend fun getItem(entityId: String): TodoItemEntity {
        return todoItemsDao.getItem(entityId)
    }

    // Добавляет или изменяет таску
    override suspend fun upsertItem(entity: TodoItemEntity) {
        return todoItemsDao.upsertItem(entity)
    }

    // Удаляет таску
    override suspend fun deleteItem(entity: TodoItemEntity) {
        return todoItemsDao.deleteItem(entity)
    }

    // Очищает таблицу
    override suspend fun deleteAllItems() {
        return todoItemsDao.deleteAllItems()
    }
}