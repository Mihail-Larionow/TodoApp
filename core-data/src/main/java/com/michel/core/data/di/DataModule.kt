package com.michel.core.data.di

import com.michel.core.data.repository.local.LocalTodoItemsRepository
import com.michel.core.data.repository.local.LocalTodoItemsRepositoryImpl
import com.michel.core.data.repository.remote.RemoteTodoItemsRepository
import com.michel.core.data.repository.remote.RemoteTodoItemsRepositoryImpl
import com.michel.database.TodoItemsDatabase
import com.michel.network.api.TodoItemsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection data module that provides repositories
 */
@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideRemoteRepository(
        api: TodoItemsApi,
    ): RemoteTodoItemsRepository {
        return RemoteTodoItemsRepositoryImpl(api)
    }

    @Provides
    fun provideLocalRepository(
        db: TodoItemsDatabase,
    ): LocalTodoItemsRepository {
        return LocalTodoItemsRepositoryImpl(db)
    }

}