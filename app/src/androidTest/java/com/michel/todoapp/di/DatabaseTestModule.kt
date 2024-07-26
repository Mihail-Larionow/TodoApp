package com.michel.todoapp.di

import android.content.Context
import androidx.room.Room
import com.michel.database.TodoItemsDatabase
import com.michel.database.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [DatabaseModule::class]
//)
//class DatabaseTestModule {
//
//    @Provides
//    @Singleton
//    fun provideTodoDatabase(@ApplicationContext context: Context): TodoItemsDatabase =
//        Room.inMemoryDatabaseBuilder(
//            context,
//            TodoItemsDatabase::class.java,
//        ).build()
//
//}