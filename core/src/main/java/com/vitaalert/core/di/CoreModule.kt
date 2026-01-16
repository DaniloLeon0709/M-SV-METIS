package com.vitaalert.core.di

import android.content.Context
import com.vitaalert.core.dispatchers.DefaultDispatcherProvider
import com.vitaalert.core.dispatchers.DispatcherProvider
import com.vitaalert.core.security.EncryptedPreferencesStorage
import com.vitaalert.core.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides core dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    /**
     * Provides secure storage for tokens and secrets.
     */
    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage = EncryptedPreferencesStorage(context)

    /**
     * Provides coroutine dispatcher configuration.
     */
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
