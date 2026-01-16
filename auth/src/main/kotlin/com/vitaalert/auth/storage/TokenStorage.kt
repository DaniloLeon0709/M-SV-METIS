package com.vitaalert.auth.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vitaalert.auth.model.AuthTokensDto

/**
 * Stores authentication tokens securely using EncryptedSharedPreferences.
 */
class TokenStorage(
    context: Context
) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Returns the stored access token, if available.
     */
    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS, null)

    /**
     * Returns the stored refresh token, if available.
     */
    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH, null)

    /**
     * Persists the provided [tokens].
     */
    fun saveTokens(tokens: AuthTokensDto) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS, tokens.accessToken)
            .putString(KEY_REFRESH, tokens.refreshToken)
            .apply()
    }

    /**
     * Clears stored tokens.
     */
    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS)
            .remove(KEY_REFRESH)
            .apply()
    }

    private companion object {
        const val FILE_NAME = "vitaalert_auth"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
