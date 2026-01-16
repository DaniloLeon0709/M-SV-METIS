package com.vitaalert.core.security

/**
 * Contract for storing sensitive data.
 */
interface SecureStorage {
    /**
     * Stores a value keyed by [key].
     */
    fun put(key: String, value: String)

    /**
     * Retrieves a value for [key] or null.
     */
    fun get(key: String): String?

    /**
     * Removes a value for [key].
     */
    fun remove(key: String)
}
