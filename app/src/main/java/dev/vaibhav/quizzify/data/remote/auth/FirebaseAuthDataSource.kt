package dev.vaibhav.quizzify.data.remote.auth

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.SafeFirebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(private val auth: FirebaseAuth) : AuthDataSource {

    override val userId: String
        get() = auth.currentUser!!.uid

    override fun logoutUser() = auth.signOut()

    override fun isUserLoggedIn() = auth.currentUser != null

    override suspend fun loginUser(email: String, password: String): Resource<String> =
        SafeFirebase.safeCall(false, "User signed in successfully") {
            auth.signInWithEmailAndPassword(email, password).await().user?.uid ?: ""
        }

    override fun removeUser() {
        auth.currentUser?.delete()
    }

    override suspend fun registerUser(
        email: String,
        username: String,
        password: String
    ): Resource<String> =
        SafeFirebase.safeCall(false, "User signed up successfully") {
            auth.createUserWithEmailAndPassword(email, password).await().user?.uid ?: ""
        }

    override suspend fun signInUsingCredential(credential: AuthCredential): Resource<FirebaseUser> =
        SafeFirebase.safeCall(false, "User signed up successfully") {
            auth.signInWithCredential(credential).await().user
        }

    override suspend fun getGoogleAccount(data: Intent): Resource<GoogleSignInAccount> =
        SafeFirebase.safeCall(true) {
            GoogleSignIn.getSignedInAccountFromIntent(data).await()
        }
}
