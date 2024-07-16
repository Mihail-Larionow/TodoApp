package com.michel.database.di

import android.content.Context
import androidx.room.Room
import com.michel.database.TodoItemsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideTodoItemsDatabase(
        @ApplicationContext context: Context
    ): TodoItemsDatabase = Room.databaseBuilder(
        context,
        TodoItemsDatabase::class.java,
        "todo_items.db"
    ).build()

}