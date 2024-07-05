package com.michel.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.michel.database.data.TodoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Database interface
 */
@Dao
interface TodoItemDao {
    @Upsert
    suspend fun upsertItem(entity: TodoItemEntity)

    @Delete
    suspend fun deleteItem(entity: TodoItemEntity)

    @Query("SELECT * FROM todo_item_table")
    fun getAll(): Flow<List<TodoItemEntity>>

    @Query("DELETE FROM todo_item_table")
    suspend fun clear()
}


