package com.michel.core.data.di

import android.content.Context
import com.michel.database.TodoItemsDatabase
import com.michel.database.TodoItemsDatabaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(db: TodoItemsDatabaseImpl): TodoItemsDatabase {
        return db
    }

    @Provides
    @Singleton
    fun provideDatabaseImplementation(@ApplicationContext context: Context): TodoItemsDatabaseImpl {
        return TodoItemsDatabaseImpl(context)
    }

}