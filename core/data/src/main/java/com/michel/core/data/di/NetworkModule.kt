package com.michel.core.data.di

import com.michel.core.data.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("TOKEN")
    internal fun providesToken(
        tokenRepository: TokenRepository
    ): String = tokenRepository.getToken() ?: "none"

}