package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import hr.ferit.helenaborzan.pregnancyhelper.model.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PregnancyInfoRepository @Inject constructor(
    accountService: AccountService,
    firestore: FirebaseFirestore
) : BaseInfoRepository(accountService, firestore) {
    override val collectionName: String
        get() = "pregnancyInfo"
    val pregnancyInfoCollection = firestore.collection(collectionName)

    fun getUsersPregnancyInfo(): Flow<List<PregnancyInfo>> = callbackFlow {
        val userId = accountService.currentUserId
        val listenerRegistration = pregnancyInfoCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val pregnancyInfo = snapshot.documents.mapNotNull { document ->
                        document.toObject(PregnancyInfo::class.java)?.apply { id = document.id }
                    }
                    trySend(pregnancyInfo).isSuccess
                } else {
                    trySend(emptyList<PregnancyInfo>()).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun createInfoDocument(userId: String) : String {
        val pregnancyInfoData = hashMapOf(
            "userId" to userId,
            "nutritionInfo" to emptyList<NutritionInfo>(),
            "contractionsInfo" to emptyList<ContractionsInfo>(),
            "questionnaireResults" to emptyList<QuestionnaireResult>()
        )
        val documentReference = collection.add(pregnancyInfoData).await()
        return documentReference.id
    }
    override suspend fun fetchQuestionnaireResults(): List<QuestionnaireResult>? {
        val documentSnapshots = getDocumentsByField("userId", accountService.currentUserId)
        return if (documentSnapshots.isNotEmpty()) {
            val document = documentSnapshots.firstOrNull()
            val pregnancyInfo = document?.toObject(PregnancyInfo::class.java)
            pregnancyInfo?.questionnaireResults
        } else {
            null
        }
    }

    override suspend fun updateSelectedAnswer(
        questionnaireId: String?,
        questionId: String,
        answer: Answer?
    ) {
        TODO("Not yet implemented")
    }

}