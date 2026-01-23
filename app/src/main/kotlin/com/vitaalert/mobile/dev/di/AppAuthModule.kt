package com.vitaalert.mobile.dev.di

import android.content.Context
import com.vitaalert.auth.repository.AuthRepository
import com.vitaalert.auth.repository.FakeAuthRepository
import com.vitaalert.auth.session.SessionManager
import com.vitaalert.auth.storage.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides authentication dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppAuthModule {
    /**
     * Provides encrypted token storage.
     */
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return TokenStorage(context)
    }

    /**
     * Provides the session manager.
     */
    @Provides
    @Singleton
    fun provideSessionManager(tokenStorage: TokenStorage): SessionManager {
        return SessionManager(tokenStorage)
    }

    /**
     * Provides the fake auth repository for runtime.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = FakeAuthRepository()
}
