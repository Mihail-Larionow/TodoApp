package com.michel.database.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity of table with TodoItems
 */
@Entity(tableName = "todo_item_table")
data class TodoItemEntity(
    @PrimaryKey
    var id: String,
    var text: String,
    var importance: String,
    var deadline: Long? = null,
    var isDone: Boolean,
    var createdAt: Long,
    var changedAt: Long? = null
)