package hr.ferit.helenaborzan.pregnancyhelper.model.service.impl

import android.util.Log
import com.google.android.gms.nearby.connection.AuthenticationException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import hr.ferit.helenaborzan.pregnancyhelper.model.data.common.User
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) } ?: User())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthenticationException("Netočan e-mail.")
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException("Korisnik nije pronađen. Molimo provjerite e-mail adresu.")
        }
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
        auth.currentUser?.let { auth.signOut() }
    }

    override suspend fun signIn(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
        }

    override suspend fun checkIfAccountExists(email: String): Boolean {
        if (!isValidEmail(email)) {
            Log.d("AccountService", "Invalid email format: $email")
            throw IllegalArgumentException("Invalid email format")
        }

        return try {
            auth.signInWithEmailAndPassword(email, "dummyPassword").await()
            true
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.d("AccountService", "Account does not exist for $email")
            false
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.d("AccountService", "Account exists for $email")
            true
        } catch (e: Exception) {
            Log.e("AccountService", "Error checking if account exists", e)
            throw e
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
