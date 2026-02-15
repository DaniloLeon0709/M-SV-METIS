package com.vitaalert.auth.google

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Helper class to manage Google Sign-In with Firebase.
 */
class GoogleSignInHelper(private val context: Context) {

    companion object {
        private const val TAG = "GoogleSignInHelper"
        // Web Client ID from google-services.json
        private const val WEB_CLIENT_ID = "1095577281902-dv8ignv47r2dg5gh3d0u400quuei14vf.apps.googleusercontent.com"
    }

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    /**
     * Returns the sign-in intent to launch.
     */
    fun getSignInIntent(): Intent {
        // Sign out first to force account picker
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    /**
     * Handles the result from the sign-in intent.
     * @return GoogleSignInAccount if successful, null otherwise.
     */
    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-In success: ${account.email}")
            account
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed with code: ${e.statusCode}, message: ${e.message}")
            when (e.statusCode) {
                12501 -> Log.e(TAG, "Sign-in cancelled by user")
                12502 -> Log.e(TAG, "Sign-in currently in progress")
                10 -> Log.e(TAG, "DEVELOPER_ERROR - Check SHA-1 fingerprint in Firebase Console")
                7 -> Log.e(TAG, "NETWORK_ERROR - Check internet connection")
                8 -> Log.e(TAG, "INTERNAL_ERROR")
                else -> Log.e(TAG, "Unknown error code: ${e.statusCode}")
            }
            null
        }
    }

    /**
     * Data class to hold Google Sign-In result information.
     */
    data class GoogleSignInResult(
        val success: Boolean,
        val userId: String? = null,
        val email: String? = null,
        val displayName: String? = null,
        val photoUrl: String? = null,
        val isNewUser: Boolean = false
    )

    /**
     * Signs in to Firebase using the Google account.
     * @return GoogleSignInResult with user information if successful.
     */
    suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): GoogleSignInResult {
        return try {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null) {
                Log.d(TAG, "Firebase auth success: ${user.email}, isNewUser: ${authResult.additionalUserInfo?.isNewUser}")
                GoogleSignInResult(
                    success = true,
                    userId = user.uid,
                    email = user.email ?: account.email,
                    displayName = user.displayName ?: account.displayName,
                    photoUrl = user.photoUrl?.toString() ?: account.photoUrl?.toString(),
                    isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                )
            } else {
                GoogleSignInResult(success = false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase auth failed: ${e.message}")
            GoogleSignInResult(success = false)
        }
    }

    /**
     * Signs out from Google.
     */
    fun signOut() {
        googleSignInClient.signOut()
    }

    /**
     * Revokes access (disconnects the app from Google account).
     */
    fun revokeAccess() {
        googleSignInClient.revokeAccess()
    }
}

