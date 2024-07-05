package com.michel.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.michel.database.dao.TodoItemDao

/**
 * Implements local database
 */
@Database(
    entities = [TodoItemEntity::class],
    version = 1
)
abstract class TodoItemsDatabase : RoomDatabase() {
    abstract val todoItemDao: TodoItemDao

    companion object {
        fun createDataBase(context: Context): TodoItemsDatabase {
            return Room.databaseBuilder(
                context,
                TodoItemsDatabase::class.java,
                "todo_items.db"
            ).build()
        }
    }
}
