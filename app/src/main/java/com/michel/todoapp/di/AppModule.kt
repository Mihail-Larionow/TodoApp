package com.michel.todoapp.di

import com.michel.todoapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Named


/**
 * Dependency Injection app module that provides properties from gradle.properties
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Named("TOKEN_OAUTH")
    fun provideOAuthToken(): String = BuildConfig.TOKEN_OAUTH

    @Provides
    @Named("TOKEN_BEARER")
    fun provideBearerToken(): String = BuildConfig.TOKEN_BEARER

    @Provides
    @Named("BASE_URL")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

}