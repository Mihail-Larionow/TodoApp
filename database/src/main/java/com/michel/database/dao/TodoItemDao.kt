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

    @Query("SELECT * FROM todo_item_table")
    fun getAllItems(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_item_table WHERE id = :itemId LIMIT 1")
    suspend fun getItem(itemId: String): TodoItemEntity

    @Upsert
    suspend fun upsertItem(entity: TodoItemEntity)

    @Delete
    suspend fun deleteItem(entity: TodoItemEntity)

    @Query("DELETE FROM todo_item_table")
    suspend fun deleteAllItems()

}


