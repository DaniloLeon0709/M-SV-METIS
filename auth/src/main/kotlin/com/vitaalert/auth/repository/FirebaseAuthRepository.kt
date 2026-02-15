package com.vitaalert.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.vitaalert.auth.model.AuthTokensDto
import com.vitaalert.auth.session.SessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación de AuthRepository usando Firebase Authentication.
 */
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthTokensDto {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Login failed: user is null")
        val idToken = user.getIdToken(false).await().token ?: ""

        return AuthTokensDto(
            accessToken = idToken,
            refreshToken = user.uid,
            tokenType = "Bearer",
            expiresIn = 3600
        )
    }

    override suspend fun refresh(refreshToken: String): AuthTokensDto {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException("Not logged in")
        val idToken = user.getIdToken(true).await().token ?: ""

        return AuthTokensDto(
            accessToken = idToken,
            refreshToken = user.uid,
            tokenType = "Bearer",
            expiresIn = 3600
        )
    }

    /**
     * Crea una nueva cuenta de usuario.
     */
    suspend fun createAccount(email: String, password: String): AuthTokensDto {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return login(email, password)
    }

    /**
     * Cierra la sesión actual.
     */
    fun signOut() {
        firebaseAuth.signOut()
        sessionManager.onUnauthenticated()
    }

    /**
     * Obtiene el UID del usuario actual.
     */
    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    /**
     * Verifica si hay un usuario autenticado.
     */
    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null
}

