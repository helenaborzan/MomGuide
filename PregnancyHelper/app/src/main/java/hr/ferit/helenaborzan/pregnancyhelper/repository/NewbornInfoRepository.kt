package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
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
            "name" to null,
            "sex" to null,
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

    suspend fun deletePercentileResult(growthAndDevelopmentResultIndex: Int, growthAndDevelopmentResults: MutableList<GrowthAndDevelopmentResult>) {

        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            if (growthAndDevelopmentResultIndex in growthAndDevelopmentResults.indices) {
                // Remove the item from the local list
                growthAndDevelopmentResults.removeAt(growthAndDevelopmentResultIndex)

                // Convert the local list to a list of maps for Firestore
                val updatedResults = growthAndDevelopmentResults.map { result ->
                    mapOf(
                        "growthAndDevelopmentInfo" to result.growthAndDevelopmentInfo.toMap(),
                        "growthAndDevelopmentPercentiles" to result.growthAndDevelopmentPercentiles.toMap()
                    )
                }

                // Update Firestore with the modified list
                val documentReference = collection.document(documentId)
                documentReference.update("growthAndDevelopmentResults", updatedResults).await()
            } else {
                throw IndexOutOfBoundsException("Invalid index for growthAndDevelopmentResults")
            }
        }
    }
    fun GrowthAndDevelopmentInfo.toMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "weight" to weight,
            "length" to length,
            "age" to age,
            "headCircumference" to headCircumference
        )
    }

    fun GrowthAndDevelopmentPercentiles.toMap(): Map<String, Any?> {
        return mapOf(
            "lengthForAgePercentile" to lengthForAgePercentile,
            "weightForAgePercentile" to weightForAgePercentile,
            "weightForLengthPercentile" to weightForLengthPercentile,
            "headCircumferenceForAgePercentile" to headCircumferenceForAgePercentile
            // Add other fields as necessary
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addBabyName(name: String) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val documentReference = collection.document(documentId)


            documentReference.update("name", name).await()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addBabySex(sex: String) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val documentReference = collection.document(documentId)

            documentReference.update("sex", sex).await()
        }
    }
}




