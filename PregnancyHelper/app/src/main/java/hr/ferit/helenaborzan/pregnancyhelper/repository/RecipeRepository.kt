package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.BuildConfig
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.model.service.EdamamApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val edamamApi: EdamamApi,
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService
) {
    suspend fun searchRecipes(query: String): Result<RecipeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<RecipeResponse> = edamamApi.getRecipes(
                    query = query,
                    appId = BuildConfig.EDAMAM_APP_ID,
                    appKey = BuildConfig.EDAMAM_APP_KEY
                ).execute()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val limitedBody = body.copy(
                            hits = body.hits.take(15)
                        )
                        Log.d("RecipeRepository", "Total recipes: ${body.hits.size}")
                        Result.success(limitedBody)
                    } else {
                        Log.e("RecipeRepository", "Response body is null")
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "RecipeRepository",
                        "API call failed with code ${response.code()}. Error body: $errorBody"
                    )
                    val errorMessage = parseErrorMessage(errorBody) ?: "Unknown error occurred"
                    Result.failure(Exception("API call failed: $errorMessage"))
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", "Error in searchRecipes", e)
                Result.failure(e)
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return errorBody

    }

    fun getFavoriteRecipes(): Flow<List<RecipeInfo>> = callbackFlow {
        val userId = accountService.currentUserId

        val listenerRegistration = firestore.collection("likes")
            .whereArrayContains("users", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val recipeInfos = snapshot?.documents?.map { doc ->
                    RecipeInfo(
                        label = doc.getString("label") ?: "",
                        image = doc.getString("image") ?: "",
                        url = doc.getString("url") ?: ""
                    )
                } ?: listOf()

                trySend(recipeInfos)
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun toggleFavorite(recipeInfo: RecipeInfo) {
        val userId = accountService.currentUserId
        Log.d("ToggleFavorite", "Current user ID: $userId")

        // Use the recipe URL as the document ID (after hashing for safety)
        val recipeId = recipeInfo.url.hashCode().toString()
        val likeRef = firestore.collection("likes").document(recipeId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(likeRef)

            if (snapshot.exists()) {
                // Document exists, update the users list
                val users = snapshot.get("users") as? List<String> ?: listOf()

                if (userId in users) {
                    // User already liked, remove from list
                    Log.d("ToggleFavorite", "Removing like for: ${recipeInfo.label}")
                    transaction.update(likeRef, "users", FieldValue.arrayRemove(userId))
                } else {
                    // User hasn't liked, add to list
                    Log.d("ToggleFavorite", "Adding like for: ${recipeInfo.label}")
                    transaction.update(likeRef, "users", FieldValue.arrayUnion(userId))
                }
            } else {
                // Document doesn't exist, create it with initial data
                Log.d("ToggleFavorite", "Creating new like document for: ${recipeInfo.label}")
                val initialData = mapOf(
                    "label" to recipeInfo.label,
                    "image" to recipeInfo.image,
                    "url" to recipeInfo.url,
                    "users" to listOf(userId),
                    "timestamp" to FieldValue.serverTimestamp()
                )
                transaction.set(likeRef, initialData)
            }
        }.addOnSuccessListener {
            Log.d("ToggleFavorite", "Successfully toggled favorite for ${recipeInfo.label}")
        }.addOnFailureListener { e ->
            Log.e("ToggleFavorite", "Error toggling favorite for ${recipeInfo.label}", e)
        }
    }

    fun getLikeCount(recipeUrl: String): Flow<Int> = callbackFlow {
        val listenerRegistration = firestore.collection("likes")
            .whereEqualTo("url", recipeUrl)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val likeCount = snapshot?.documents?.firstOrNull()?.let { doc ->
                    (doc.get("users") as? List<*>)?.size ?: 0
                } ?: 0

                trySend(likeCount)
            }

        awaitClose { listenerRegistration.remove() }
    }
}