package com.michel.database.di

import com.michel.database.TodoItemsDatabase
import com.michel.database.dao.RevisionDao
import com.michel.database.dao.TodoItemsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesTodoItemsDao(
        database: TodoItemsDatabase
    ): TodoItemsDao = database.todoItemsDao

    @Provides
    fun providesRevisionDao(
        database: TodoItemsDatabase
    ): RevisionDao = database.revisionDao

}