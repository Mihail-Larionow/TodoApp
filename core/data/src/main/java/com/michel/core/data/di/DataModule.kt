package com.michel.core.data.di

import com.michel.core.data.datasource.local.LocalDataSource
import com.michel.core.data.datasource.local.LocalDataSourceImpl
import com.michel.core.data.datasource.remote.RemoteDataSource
import com.michel.core.data.datasource.remote.RemoteDataSourceImpl
import com.michel.core.data.repository.TodoItemsRepository
import com.michel.core.data.repository.TodoItemsRepositoryImpl
import com.michel.core.data.repository.TokenRepository
import com.michel.core.data.repository.TokenRepositoryImpl
import com.michel.core.data.repository.WorkerRepository
import com.michel.core.data.repository.WorkerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

/**
 * Dependency Injection data module that provides repositories
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsLocalDataSource(
        localDataSource: LocalDataSourceImpl,
    ): LocalDataSource

    @Binds
    internal abstract fun bindsRemoteDataSource(
        remoteDataSource: RemoteDataSourceImpl
    ): RemoteDataSource

    @Binds
    internal abstract fun bindsTokenRepository(
        tokenRepository: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    internal abstract fun bindsTodoItemsRepository(
        todoItemsRepository: TodoItemsRepositoryImpl
    ): TodoItemsRepository

    @Binds
    internal abstract fun bindsWorkerRepository(
        workerRepository: WorkerRepositoryImpl
    ): WorkerRepository


}