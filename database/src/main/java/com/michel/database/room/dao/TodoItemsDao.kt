package com.michel.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.michel.database.room.models.TodoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Database interface to work with todo items table
 */
@Dao
internal interface TodoItemsDao {

    @Query("SELECT * FROM todo_item_table")
    fun getAllItemsFlow(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_item_table")
    suspend fun getAllItems(): List<TodoItemEntity>

    @Query("SELECT * FROM todo_item_table WHERE id = :itemId LIMIT 1")
    suspend fun getItem(itemId: String): TodoItemEntity

    @Upsert
    suspend fun upsertItem(entity: TodoItemEntity)

    @Delete
    suspend fun deleteItem(entity: TodoItemEntity)

    @Query("DELETE FROM todo_item_table")
    suspend fun deleteAllItems()

}


