package com.michel.core.data.di

import android.content.Context
import com.michel.core.data.repository.TokenRepository
import com.michel.network.api.TodoItemsApi
import com.michel.network.api.TodoItemsApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideApi(api: TodoItemsApiImpl): TodoItemsApi {
        return api
    }

    @Provides
    fun provideApiImplementation(
        @ApplicationContext context: Context,
        @Named("URL") baseUrl: String,
        @Named("TOKEN") token: String,
    ): TodoItemsApiImpl {
        return TodoItemsApiImpl(
            context = context,
            baseUrl = baseUrl,
            token = token
        )
    }

    @Provides
    @Named("TOKEN")
    fun provideToken(tokenRepository: TokenRepository): String {
        return tokenRepository.getToken() ?: "none"
    }

}