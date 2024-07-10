package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

abstract class BaseInfoRepository(
    protected val accountService: AccountService,
    protected val firestore: FirebaseFirestore
) {
    protected abstract val collectionName: String
    protected val collection by lazy { firestore.collection(collectionName) }


    suspend fun ensureInfoDocument() {
        val userId = accountService.currentUserId
        val infoDocRef = collection.whereEqualTo("userId", userId).get().await()

        if (infoDocRef.isEmpty) {
            createInfoDocument(userId)
        }
    }

    protected abstract suspend fun createInfoDocument(userId: String)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addQuestionnaireResults(score: Int, resultMessage: String) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "resultMessage" to resultMessage,
                "score" to score,
                "date" to Timestamp.now()
            )

            val documentReference = collection.document(documentId)
            documentReference.update("questionnaireResults", FieldValue.arrayUnion(newResult)).await()
        }
    }
}