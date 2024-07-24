package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.QandA
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
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


    suspend fun ensureInfoDocument():String {
        val userId = accountService.currentUserId
        val infoDocRef = collection.whereEqualTo("userId", userId).get().await()

        return if (infoDocRef.isEmpty) {
            createInfoDocument(userId)
        } else {
            infoDocRef.documents[0].id
        }
    }

    protected abstract suspend fun createInfoDocument(userId: String): String

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addQuestionnaireResults(questionnaireResultId: String, score: Int, resultMessage: String) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "id" to questionnaireResultId,
                "resultMessage" to resultMessage,
                "score" to score,
                "date" to Timestamp.now()
            )

            val documentReference = collection.document(documentId)

            // First, we'll check if a result with this ID already exists
            val currentData = document.data
            val questionnaireResults = currentData?.get("questionnaireResults") as? List<Map<String, Any>> ?: listOf()

            val existingResultIndex = questionnaireResults.indexOfFirst { it["id"] == questionnaireResultId }

            if (existingResultIndex != -1) {
                // If the result exists, update it
                val updatedResults = questionnaireResults.toMutableList()
                updatedResults[existingResultIndex] = newResult
                documentReference.update("questionnaireResults", updatedResults).await()
            } else {
                // If the result doesn't exist, add it
                documentReference.update("questionnaireResults", FieldValue.arrayUnion(newResult)).await()
            }
        }
    }
    protected suspend fun getDocumentById(documentId: String): DocumentSnapshot? {
        return try {
            collection.document(documentId).get().await()
        } catch (e: Exception) {
            Log.e("BaseInfoRepository", "Error getting document", e)
            null
        }
    }

    protected suspend fun getDocumentsByField(fieldName: String, value: Any): List<DocumentSnapshot> {
        return try {
            collection.whereEqualTo(fieldName, value).get().await().documents
        } catch (e: Exception) {
            Log.e("BaseInfoRepository", "Error querying documents", e)
            emptyList()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initializeQuestionnaireResult(questionnaireId: String) {
        val documentId = ensureInfoDocument()
        val initialResult = hashMapOf(
            "id" to questionnaireId,
            "date" to Timestamp.now(),
            "results" to listOf<QandA>(),
            "score" to 0,
            "resultMessage" to ""
        )

        val documentReference = collection.document(documentId)
        documentReference.update("questionnaireResults", FieldValue.arrayUnion(initialResult)).await()
    }
    abstract suspend fun fetchQuestionnaireResults(): List<QuestionnaireResult>?
    abstract suspend fun updateSelectedAnswer(questionnaireId: String?,questionId: String, answer: Answer?)
}