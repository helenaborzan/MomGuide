package hr.ferit.helenaborzan.pregnancyhelper.model.service.impl

import androidx.compose.ui.util.trace
import com.google.android.gms.nearby.connection.AuthenticationException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import hr.ferit.helenaborzan.pregnancyhelper.model.User
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
            true
        } catch (e: FirebaseAuthInvalidUserException) {
            // No such user exists
            false
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Invalid credentials means account exists but wrong password
            auth.signOut()
            true
        }
    }

}
