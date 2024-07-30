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
        return try {
            auth.signInWithEmailAndPassword(email, "dummyPassword").await()
            // Sign-in successful means account exists
            auth.signOut()
            Log.d("AccountService", "Account exists for email: $email")
            true
        } catch (e: FirebaseAuthInvalidUserException) {
            // No such user exists
            Log.d("AccountService", "No account exists for email: $email")
            false
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Invalid credentials means account exists but wrong password
            auth.signOut()
            Log.d("AccountService", "Account exists for email: $email but invalid credentials used")
            true
        } catch (e: Exception) {
            // Catch any other exceptions and log them
            Log.e("AccountService", "Error checking account existence for email: $email", e)
            false
        }
    }

}
