package com.michel.core.data.di

import android.content.Context
import com.michel.core.data.repository.LocalTodoItemsRepository
import com.michel.core.data.repository.RemoteTodoItemsRepository
import com.michel.core.data.repository.TokenRepository
import com.michel.database.data.TodoItemsDatabase
import com.michel.network.api.backend.TodoItemsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection data module that provides repositories
 */
@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideRemoteRepository(
        api: TodoItemsApi,
        tokenRepo: TokenRepository
    ): RemoteTodoItemsRepository {
        return RemoteTodoItemsRepository(api = api, tokenRepo = tokenRepo)
    }

    @Provides
    @Singleton
    fun provideLocalRepository(@ApplicationContext context: Context): LocalTodoItemsRepository {
        val db = TodoItemsDatabase.createDataBase(context)
        return LocalTodoItemsRepository(db)
    }

}