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
    @Named("TOKEN")
    fun provideToken(): String = BuildConfig.TODOAPP_TOKEN

    @Provides
    @Singleton
    @Named("URL")
    fun provideBaseUrl(): String = BuildConfig.TODOAPP_BASE_URL

}