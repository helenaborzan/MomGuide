package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import com.google.type.DateTime
import hr.ferit.helenaborzan.pregnancyhelper.model.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.Food
import hr.ferit.helenaborzan.pregnancyhelper.model.FoodInfo
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject

class PregnancyInfoRepository @Inject constructor(
    accountService: AccountService,
    firestore: FirebaseFirestore
) : BaseInfoRepository(accountService, firestore) {
    override val collectionName: String
        get() = "pregnancyInfo"
    val pregnancyInfoCollection = firestore.collection(collectionName)

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createInfoDocument(userId: String): String {
        val pregnancyInfoData = hashMapOf(
            "userId" to userId,
            "pregnancyStartDate" to null,
            "dailyCalorieGoal" to null,
            "nutritionInfo" to emptyList<NutritionInfo>(),
            "contractionsInfo" to emptyList<ContractionsInfo>(),
            "questionnaireResults" to emptyList<QuestionnaireResult>()
        )
        val documentReference = collection.add(pregnancyInfoData).await()
        return documentReference.id
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchQuestionnaireResult(questionnaireId: String): QuestionnaireResult? {
        val documentSnapshots = getDocumentsByField("userId", accountService.currentUserId)
        return if (documentSnapshots.isNotEmpty()) {
            val document = documentSnapshots.firstOrNull()
            val pregnancyInfo = document?.toObject(PregnancyInfo::class.java)
            pregnancyInfo?.questionnaireResults?.find { it.id == questionnaireId }
        } else {
            null
        }
    }


    override suspend fun updateSelectedAnswer(
        questionnaireId: String,
        questionId: String,
        answer: Answer?
    ) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPregnancyStartDate(pregnancyStartDate: Timestamp) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val documentReference = collection.document(documentId)


            documentReference.update("pregnancyStartDate", pregnancyStartDate).await()
        }
    }

    suspend fun addUsersFoodIntake(food: Food) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val documentReference = collection.document(documentId)

            // Kreiramo FoodInfo objekt iz Food objekta
            val foodInfo = FoodInfo(
                foodName = food.food_name,
                calories = food.nf_calories,
                totalFat = food.nf_total_fat,
                carbohydrate = food.nf_total_carbohydrate,
                protein = food.nf_protein,
                dietaryFiber = food.nf_dietary_fiber,
                sugars = food.nf_sugars,
                sodium = food.nf_sodium,
                servingQuantity = food.serving_qty,
                servingUnit = food.serving_unit,
                servingWeight = food.serving_weight_grams
            )

            // Kreiramo Timestamp za današnji datum (samo datum, bez vremena)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            val todayTimestamp = Timestamp(today)

            val nutritionInfoArray = document.get("nutritionInfo") as? List<Map<String, Any>> ?: listOf()

            // Tražimo element za današnji datum
            val todayNutritionInfoIndex = nutritionInfoArray.indexOfFirst {
                (it["date"] as? Timestamp)?.toDate()?.time == todayTimestamp.toDate().time
            }

            if (todayNutritionInfoIndex != -1) {
                // Ako postoji element za danas, dodajemo novo FoodInfo
                val updatedNutritionInfoArray = nutritionInfoArray.toMutableList()
                val todayNutritionInfo = updatedNutritionInfoArray[todayNutritionInfoIndex].toMutableMap()
                val foodInfoList = (todayNutritionInfo["foodInfo"] as? List<Map<String, Any?>> ?: listOf()).toMutableList()
                foodInfoList.add(foodInfo.toMap())
                todayNutritionInfo["foodInfo"] = foodInfoList
                updatedNutritionInfoArray[todayNutritionInfoIndex] = todayNutritionInfo
                documentReference.update("nutritionInfo", updatedNutritionInfoArray).await()
            } else {
                // Ako ne postoji element za danas, kreiramo novi NutritionInfo
                val newNutritionInfo = mapOf(
                    "date" to todayTimestamp,
                    "foodInfo" to listOf(foodInfo.toMap())
                )
                documentReference.update("nutritionInfo", FieldValue.arrayUnion(newNutritionInfo)).await()
            }
        }
    }

    fun FoodInfo.toMap(): Map<String, Any?> {
        return mapOf(
            "foodName" to foodName,
            "calories" to calories,
            "totalFat" to totalFat,
            "carbohydrate" to carbohydrate,
            "protein" to protein,
            "dietaryFiber" to dietaryFiber,
            "sugars" to sugars,
            "sodium" to sodium,
            "servingQuantity" to servingQuantity,
            "servingUnit" to servingUnit,
            "servingWeight" to servingWeight
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateDailyCalorieGoal(dailyCalorieGoal: Double) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id
            val documentReference = collection.document(documentId)

            documentReference.update("dailyCalorieGoal", dailyCalorieGoal).await()
        }
    }
}