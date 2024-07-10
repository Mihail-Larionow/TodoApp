package com.michel.todoapp.di

import com.michel.todoapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


/**
 * Dependency Injection app module that provides properties from gradle.properties
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    @Named("TOKEN_OAUTH")
    fun provideOAuthToken(): String = BuildConfig.TOKEN_OAUTH

    @Provides
    @Singleton
    @Named("TOKEN_BEARER")
    fun provideBearerToken(): String = BuildConfig.TOKEN_BEARER

    @Provides
    @Singleton
    @Named("URL")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

}