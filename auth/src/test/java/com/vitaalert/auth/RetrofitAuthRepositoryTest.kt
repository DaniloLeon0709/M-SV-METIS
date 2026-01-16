package com.vitaalert.auth

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.vitaalert.auth.api.AuthApi
import com.vitaalert.auth.repository.RetrofitAuthRepository
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * Unit tests for RetrofitAuthRepository using MockWebServer.
 */
class RetrofitAuthRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: RetrofitAuthRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        repository = RetrofitAuthRepository(retrofit.create(AuthApi::class.java))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `login returns tokens`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                "{\"access_token\":\"access-123\",\"refresh_token\":\"refresh-123\"}"
            )
        )

        val tokens = repository.login("demo", "demo")

        assertEquals("access-123", tokens.accessToken)
        assertEquals("refresh-123", tokens.refreshToken)
    }

    @Test
    fun `refresh returns tokens`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                "{\"access_token\":\"access-456\",\"refresh_token\":\"refresh-456\"}"
            )
        )

        val tokens = repository.refresh()

        assertEquals("access-456", tokens.accessToken)
        assertEquals("refresh-456", tokens.refreshToken)
    }
}
