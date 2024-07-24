package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import hr.ferit.helenaborzan.pregnancyhelper.model.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.QandA
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class NewbornInfoRepository @Inject constructor(
    accountService: AccountService,
    firestore: FirebaseFirestore
) : BaseInfoRepository(accountService, firestore) {
    override val collectionName: String
        get() = "newbornInfo"
    val newbornInfoCollection = firestore.collection(collectionName)

    fun getUsersNewbornInfo(): Flow<List<NewbornInfo>> = callbackFlow {
        val userId = accountService.currentUserId
        val listenerRegistration = newbornInfoCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val newbornInfo = snapshot.documents.mapNotNull { document ->
                        document.toObject(NewbornInfo::class.java)?.apply { id = document.id }
                    }
                    trySend(newbornInfo).isSuccess
                } else {
                    trySend(emptyList<NewbornInfo>()).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun createInfoDocument(userId: String) : String {
        val newbornInfoData = hashMapOf(
            "userId" to userId,
            "breastfeedingInfo" to emptyList<BreastfeedingInfo>(),
            "bottleInfo" to emptyList<BottleInfo>(),
            "growthAndDevelopmentResults" to emptyList<GrowthAndDevelopmentResult>(),
            "questionnaireResults" to emptyList<QuestionnaireResult>()
        )
        val documentReference = collection.add(newbornInfoData).await()
        return documentReference.id
    }

    suspend fun addGrowthAndDevelopmentResult(
        growthAndDevelopmentInfo: GrowthAndDevelopmentInfo,
        growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles
    ) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "growthAndDevelopmentInfo" to growthAndDevelopmentInfo,
                "growthAndDevelopmentPercentiles" to growthAndDevelopmentPercentiles
            )

            val documentReference = collection.document(documentId)
            documentReference.update("growthAndDevelopmentResults", FieldValue.arrayUnion(newResult)).await()
        }
    }

    suspend fun addBreastfeedingInfo(
        breastfeedingInfo: BreastfeedingInfo
    ){
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "startTime" to breastfeedingInfo.startTime,
                "endTime" to breastfeedingInfo.endTime,
                "breast" to breastfeedingInfo.breast
            )

            val documentReference = collection.document(documentId)
            documentReference.update("breastfeedingInfo", FieldValue.arrayUnion(newResult)).await()
        }
    }

    suspend fun addBottleInfo(
        bottleInfo: BottleInfo
    ){
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "time" to bottleInfo.time,
                "quantity" to bottleInfo.quantity
            )

            val documentReference = collection.document(documentId)
            documentReference.update("bottleInfo", FieldValue.arrayUnion(newResult)).await()
        }
    }

    suspend fun deletePercentileResult(growthAndDevelopmentResultIndex: Int) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val currentResults = document.get("growthAndDevelopmentResults") as? List<Map<String, Any>> ?: listOf()

            if (growthAndDevelopmentResultIndex in currentResults.indices) {
                val updatedResults = currentResults.toMutableList()
                updatedResults.removeAt(growthAndDevelopmentResultIndex)

                val documentReference = collection.document(documentId)
                documentReference.update("growthAndDevelopmentResults", updatedResults).await()
            } else {
                throw IndexOutOfBoundsException("Invalid index for growthAndDevelopmentResults")
            }
        }
    }

    override suspend fun fetchQuestionnaireResults(): List<QuestionnaireResult>? {
        val documentSnapshots = getDocumentsByField("userId", accountService.currentUserId)
        return if (documentSnapshots.isNotEmpty()) {
            val document = documentSnapshots.firstOrNull()
            val newbornInfo = document?.toObject(NewbornInfo::class.java)
            newbornInfo?.questionnaireResults
        } else {
            null
        }
    }

    override suspend fun updateSelectedAnswer(questionnaireId: String?, questionId: String, answer: Answer?) {
        val userId = accountService.currentUserId
        val documentSnapshots = getDocumentsByField("userId", userId)

        if (documentSnapshots.isNotEmpty()) {
            val document = documentSnapshots.firstOrNull()
            document?.let {
                val documentReference = it.reference

                // Fetch current newborn info
                val currentNewbornInfo = it.toObject(NewbornInfo::class.java) ?: NewbornInfo(userId = userId)

                // Create a new QandA entry
                val newQandA = QandA(questionId = questionId, selectedAnswer = answer)

                // Find the specific QuestionnaireResult by id
                val questionnaireResultIndex = currentNewbornInfo.questionnaireResults.indexOfFirst { it.id == questionnaireId }

                if (questionnaireResultIndex != -1) {
                    // QuestionnaireResult exists, update it
                    val currentResult = currentNewbornInfo.questionnaireResults[questionnaireResultIndex]
                    val updatedResults = currentResult.results?.toMutableList() ?: mutableListOf()

                    val existingQandAIndex = updatedResults.indexOfFirst { it.questionId == questionId }
                    if (existingQandAIndex != -1) {
                        // Update existing QandA
                        updatedResults[existingQandAIndex] = newQandA
                    } else {
                        // Add new QandA
                        updatedResults.add(newQandA)
                    }

                    currentNewbornInfo.questionnaireResults[questionnaireResultIndex] = currentResult.copy(
                        results = updatedResults
                    )
                } else {
                    // QuestionnaireResult doesn't exist, create a new one
                    val newResult = QuestionnaireResult(
                        id = questionnaireId,
                        date = Timestamp.now(),
                        results = listOf(newQandA)
                    )
                    currentNewbornInfo.questionnaireResults.add(newResult)
                }

                // Save the updated newborn info to Firestore
                try {
                    documentReference.set(currentNewbornInfo).await()
                    Log.d("Firestore Update", "Successfully updated Firestore with new answer.")
                } catch (e: Exception) {
                    Log.e("Firestore Update", "Error updating Firestore with new answer", e)
                }
            }
        } else {
            Log.e("Firestore Update", "No document found for user $userId")
        }
    }
}