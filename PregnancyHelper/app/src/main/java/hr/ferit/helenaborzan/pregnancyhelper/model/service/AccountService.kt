package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.User
import kotlinx.coroutines.flow.Flow


interface AccountService {
    val currentUserId : String
    val currentUser : Flow<User>

    suspend fun authenticate(email : String, password : String)

    suspend fun deleteAccount()

    suspend fun signOut()

    suspend fun signIn(email: String, password: String)

    suspend fun sendRecoveryEmail(email: String)

    suspend fun checkIfAccountExists(email: String): Boolean

}