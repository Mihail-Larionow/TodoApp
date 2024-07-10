package com.michel.database.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.michel.database.room.models.RevisionEntity

/**
 * Database interface to work with revision table
 */
@Dao
interface RevisionDao {
    @Query("SELECT revision from revision_table")
    fun get(): Int?

    @Upsert
    fun set(entity: RevisionEntity)
}