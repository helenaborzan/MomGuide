package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.util.Log
import hr.ferit.helenaborzan.pregnancyhelper.BuildConfig
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.service.EdamamApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val edamamApi: EdamamApi
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
        // Implement a method to parse the error message from the error body if needed
        return errorBody

    }
}