package com.michel.database.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revision_table")
data class RevisionEntity(
    @PrimaryKey
    val id: Int = 1,
    @ColumnInfo(name = "revision")
    val revision: Int
)