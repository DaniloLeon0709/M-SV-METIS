package com.vitaalert.auth.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.vitaalert.auth.api.AuthApi
import com.vitaalert.auth.repository.InMemoryAuthRepository
import com.vitaalert.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Hilt module for auth dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    /**
     * Provides the in-memory auth repository for runtime.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(repository: InMemoryAuthRepository): AuthRepository = repository

    /**
     * Provides the JSON configuration for serialization.
     */
    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    /**
     * Provides the OkHttp client for Retrofit.
     */
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    /**
     * Provides Retrofit configured with kotlinx.serialization.
     */
    @Provides
    fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://localhost/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /**
     * Provides the Auth API interface.
     */
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
}
