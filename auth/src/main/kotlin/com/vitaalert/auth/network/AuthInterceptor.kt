package com.vitaalert.auth.network

import com.vitaalert.auth.storage.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that adds the bearer token to requests when available.
 */
class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getAccessToken()
        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}
