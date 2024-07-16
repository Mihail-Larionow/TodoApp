package com.michel.network.di

import android.content.Context
import com.michel.network.backend.OAuthHeaderInterceptor
import com.michel.network.backend.TodoItemsApi
import com.michel.network.service.TodoItemsService
import com.michel.network.service.TodoItemsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

internal const val MAX_IDLE_CONNECTIONS_COUNT = 3
internal const val KEEP_ALIVE_DURATION_MINUTES = 5L
internal const val CONNECTION_TIMEOUT = 10000L
internal const val READ_WRITE_TIMEOUT = 10000L

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    internal abstract fun bindsTodoItemsService(
        todoItemsService: TodoItemsServiceImpl
    ): TodoItemsService

    companion object {

        @Provides
        @Singleton
        internal fun providesTodoItemsService(
            @ApplicationContext context: Context,
            todoItemsApi: TodoItemsApi,
        ): TodoItemsServiceImpl {
            return TodoItemsServiceImpl(
                context = context,
                api = todoItemsApi
            )
        }

        @Provides
        @Singleton
        internal fun providesTodoItemsApi(
            retrofit: Retrofit
        ): TodoItemsApi = retrofit
            .create(TodoItemsApi::class.java)

        @Provides
        @Singleton
        internal fun providesRetrofit(
            @Named("BASE_URL") baseUrl: String,
            client: OkHttpClient,
        ): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

        @Provides
        @Singleton
        internal fun providesHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            oauthInterceptor: OAuthHeaderInterceptor,
        ): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectionPool(
                ConnectionPool(
                    maxIdleConnections = MAX_IDLE_CONNECTIONS_COUNT,
                    keepAliveDuration = KEEP_ALIVE_DURATION_MINUTES,
                    timeUnit = TimeUnit.MINUTES
                )
            )
            .addInterceptor(loggingInterceptor)
            .addInterceptor(oauthInterceptor)
            .build()

        @Provides
        @Singleton
        internal fun providesOauthHeaderInterceptor(
            @Named("TOKEN_OAUTH") token: String,
        ): OAuthHeaderInterceptor = OAuthHeaderInterceptor(token)

        @Provides
        @Singleton
        fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    }

}