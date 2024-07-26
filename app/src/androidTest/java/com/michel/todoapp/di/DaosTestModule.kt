package com.michel.todoapp.di

import com.michel.database.TodoItemsDatabase
import com.michel.database.dao.RevisionDao
import com.michel.database.dao.TodoItemsDao
import com.michel.database.di.DaosModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [DaosModule::class]
//)
//class DaosTestModule {
//
//    @Provides
//    fun providesTodoItemsDao(
//        database: TodoItemsDatabase
//    ): TodoItemsDao = database.todoItemsDao
//
//    @Provides
//    fun providesRevisionDao(
//        database: TodoItemsDatabase
//    ): RevisionDao = database.revisionDao
//
//}