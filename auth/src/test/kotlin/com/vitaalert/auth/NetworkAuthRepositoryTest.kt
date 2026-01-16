package com.vitaalert.auth

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.vitaalert.auth.network.AuthApi
import com.vitaalert.auth.network.NetworkAuthRepository
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

/**
 * Unit tests for [NetworkAuthRepository] using MockWebServer.
 */
class NetworkAuthRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: NetworkAuthRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        repository = NetworkAuthRepository(retrofit.create(AuthApi::class.java))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `login success returns tokens`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                "{" +
                    "\"access_token\":\"access-123\"," +
                    "\"refresh_token\":\"refresh-123\"," +
                    "\"token_type\":\"Bearer\"," +
                    "\"expires_in\":3600" +
                    "}"
            )
        )

        val tokens = repository.login("user@test.com", "Password123!")

        assertEquals("access-123", tokens.accessToken)
        assertEquals("refresh-123", tokens.refreshToken)
        assertEquals("Bearer", tokens.tokenType)
        assertEquals(3600, tokens.expiresIn)
    }

    @Test
    fun `login 401 throws http exception`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401))

        assertThrows(HttpException::class.java) { repository.login("user@test.com", "Password123!") }
    }

    @Test
    fun `refresh success returns tokens`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                "{" +
                    "\"access_token\":\"access-456\"," +
                    "\"refresh_token\":\"refresh-456\"," +
                    "\"token_type\":\"Bearer\"," +
                    "\"expires_in\":7200" +
                    "}"
            )
        )

        val tokens = repository.refresh("refresh-456")

        assertEquals("access-456", tokens.accessToken)
        assertEquals("refresh-456", tokens.refreshToken)
        assertEquals("Bearer", tokens.tokenType)
        assertEquals(7200, tokens.expiresIn)
    }

    @Test
    fun `refresh 401 throws http exception`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401))

        assertThrows(HttpException::class.java) { repository.refresh("refresh-456") }
    }
}
