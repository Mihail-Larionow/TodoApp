package com.michel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michel.database.dao.RevisionDao
import com.michel.database.dao.TodoItemsDao
import com.michel.database.models.RevisionEntity
import com.michel.database.models.TodoItemEntity

/**
 * Implements room database
 */
@Database(
    entities = [TodoItemEntity::class, RevisionEntity::class],
    version = 1
)
abstract class TodoItemsDatabase : RoomDatabase() {
    abstract val todoItemsDao: TodoItemsDao
    abstract val revisionDao: RevisionDao
}
