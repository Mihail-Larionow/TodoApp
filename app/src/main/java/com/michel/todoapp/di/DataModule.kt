package com.michel.todoapp.di

import com.michel.core.date.TodoItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideTodoItemsRepository(): TodoItemsRepository  = TodoItemsRepository()

}
