package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class NewbornInfoRepository @Inject constructor(
    private val accountService: AccountService,
    private val firestore : FirebaseFirestore
){
    private val newbornInfoCollection = firestore.collection("newbornInfo")

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

    suspend fun ensureNewbornInfoDocument() {
        val userId = accountService.currentUserId
        val newbornInfoDocRef = newbornInfoCollection.whereEqualTo("userId", userId).get().await()

        if (newbornInfoDocRef.isEmpty) {
            createNewbornInfoDocument(userId)
        }
    }

    private suspend fun createNewbornInfoDocument(userId: String) {
        val newbornInfoData = hashMapOf(
            "userId" to userId,
            "breastfeedingInfo" to emptyList<DateTime>(),
            "growthAndDevelopmentResults" to emptyList<GrowthAndDevelopmentResult>(),
            "questionnaireResults" to emptyList<QuestionnaireResult>()
        )
        newbornInfoCollection.add(newbornInfoData)
            .await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addQuestionnaireResults(score: Int, resultMessage: String) {
        val userId = accountService.currentUserId
        val querySnapshot = newbornInfoCollection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "resultMessage" to resultMessage,
                "score" to score,
                "date" to Timestamp.now()
            )

            val documentReference = newbornInfoCollection.document(documentId)
            documentReference.update("questionnaireResults", FieldValue.arrayUnion(newResult)).await()
        }
    }

    suspend fun addGrowthAndDevelopmentResult(growthAndDevelopmentInfo: GrowthAndDevelopmentInfo, growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles ){
        val userId = accountService.currentUserId
        val querySnapshot = newbornInfoCollection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val newResult = hashMapOf(
                "growthAndDevelopmentInfo" to growthAndDevelopmentInfo,
                "growthAndDevelopmentPercentiles" to growthAndDevelopmentPercentiles
            )

            val documentReference = newbornInfoCollection.document(documentId)
            documentReference.update("growthAndDevelopmentResults", FieldValue.arrayUnion(newResult)).await()
        }

    }

    suspend fun deletePercentileResult(growthAndDevelopmentResult: GrowthAndDevelopmentResult) {
        val userId = accountService.currentUserId
        val querySnapshot = newbornInfoCollection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val resultToRemove = hashMapOf(
                "growthAndDevelopmentInfo" to growthAndDevelopmentResult.growthAndDevelopmentInfo,
                "growthAndDevelopmentPercentiles" to growthAndDevelopmentResult.growthAndDevelopmentPercentiles
            )

            val documentReference = newbornInfoCollection.document(documentId)
            documentReference.update("growthAndDevelopmentResults", FieldValue.arrayRemove(resultToRemove)).await()
        }
    }
}
