package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.util.Log
import com.google.gson.Gson
import hr.ferit.helenaborzan.pregnancyhelper.BuildConfig
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.service.NutritionixApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import retrofit2.Callback

class FoodRepository @Inject constructor(
    private val nutritionixApi: NutritionixApi,
    private val gson: Gson
) {
    suspend fun searchFood(query: String): Result<NutritionixResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<NutritionixResponse> = nutritionixApi.searchFood(
                    query = query,
                    appId = BuildConfig.NUTRITIONIX_APP_ID,
                    appKey = BuildConfig.NUTRITIONIX_APP_KEY
                ).execute()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("FoodRepository", "Common items: ${body.common.size}, Branded items: ${body.branded.size}")
                        Result.success(body)
                    } else {
                        Log.e("FoodRepository", "Response body is null")
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("FoodRepository", "API call failed with code ${response.code()}. Error body: $errorBody")
                    val errorMessage = parseErrorMessage(errorBody) ?: "Unknown error occurred"
                    Result.failure(Exception("API call failed: $errorMessage"))
                }
            } catch (e: Exception) {
                Log.e("FoodRepository", "Error in searchFood", e)
                Result.failure(e)
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.message
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error parsing error response", e)
            null
        }
    }
}

data class ErrorResponse(
    val message: String,
    val id: String
)