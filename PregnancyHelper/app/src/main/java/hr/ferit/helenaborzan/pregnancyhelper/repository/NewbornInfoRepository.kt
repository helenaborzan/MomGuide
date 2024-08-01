package hr.ferit.helenaborzan.pregnancyhelper.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NewbornInfoRepository @Inject constructor(
    accountService: AccountService,
    firestore: FirebaseFirestore,
    questionnaireRepository: QuestionnaireRepository
) : BaseInfoRepository(accountService, firestore, questionnaireRepository) {
    override val collectionName: String
        get() = "newbornInfo"
    val newbornInfoCollection = firestore.collection(collectionName)

    override fun getUserInfo(): Flow<List<NewbornInfo>> = callbackFlow {
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

    override suspend fun createInfoDocument(userId: String) {
        val newbornInfoData = hashMapOf(
            "userId" to userId,
            "breastfeedingInfo" to emptyList<BreastfeedingInfo>(),
            "bottleInfo" to emptyList<BottleInfo>(),
            "growthAndDevelopmentResults" to emptyList<GrowthAndDevelopmentResult>(),
            "questionnaireResults" to listOf<QuestionnaireResult>()
        )
        val documentReference = collection.add(newbornInfoData).await()
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
            documentReference.update(
                "growthAndDevelopmentResults",
                FieldValue.arrayUnion(newResult)
            ).await()
        }
    }

    suspend fun addBreastfeedingInfo(
        breastfeedingInfo: BreastfeedingInfo
    ) {
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
    ) {
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

            val currentResults =
                document.get("growthAndDevelopmentResults") as? List<Map<String, Any>> ?: listOf()

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
}




