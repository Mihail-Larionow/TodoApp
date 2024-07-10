package com.michel.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.michel.database.room.dao.RevisionDao
import com.michel.database.room.dao.TodoItemsDao
import com.michel.database.room.models.RevisionEntity
import com.michel.database.room.models.TodoItemEntity

/**
 * Implements room database
 */
@Database(
    entities = [TodoItemEntity::class, RevisionEntity::class],
    version = 1
)
internal abstract class RoomTodoItemsDatabase : RoomDatabase() {
    abstract val todoItemsDao: TodoItemsDao
    abstract val revisionDao: RevisionDao

    companion object {

        @Volatile
        private var instance: RoomTodoItemsDatabase? = null

        fun getInstance(context: Context): RoomTodoItemsDatabase {
            return instance ?: synchronized(this) {
                instance ?: createDataBase(context).also {
                    instance = it
                }
            }
        }

        private fun createDataBase(context: Context): RoomTodoItemsDatabase {
            return Room.databaseBuilder(
                context,
                RoomTodoItemsDatabase::class.java,
                "todo_items.db"
            ).build()
        }
    }
}
