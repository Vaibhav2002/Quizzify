package dev.vaibhav.quizzify.data.remote.auth

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dev.vaibhav.quizzify.util.Resource

interface AuthDataSource {

    val userId: String

    fun logoutUser()

    fun isUserLoggedIn(): Boolean

    suspend fun loginUser(email: String, password: String): Resource<String>

    fun removeUser()

    suspend fun registerUser(email: String, username: String, password: String): Resource<String>

    suspend fun signInUsingCredential(credential: AuthCredential): Resource<FirebaseUser>

    suspend fun getGoogleAccount(data: Intent): Resource<GoogleSignInAccount>
}