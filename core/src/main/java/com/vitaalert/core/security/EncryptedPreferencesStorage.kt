package com.vitaalert.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

/**
 * Secure storage backed by EncryptedSharedPreferences.
 */
class EncryptedPreferencesStorage @Inject constructor(
    context: Context
) : SecureStorage {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "vitaalert_secure_store",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun put(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun get(key: String): String? = sharedPreferences.getString(key, null)

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
