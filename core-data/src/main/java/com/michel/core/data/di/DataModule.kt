package com.michel.core.data.di

import com.michel.core.data.repository.IRepository
import com.michel.core.data.repository.TodoItemsRepository
import com.michel.network.api.BackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideTodoItemsRepository(api: BackendApi): IRepository {
        return TodoItemsRepository(api = api)
    }

}