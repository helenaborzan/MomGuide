package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.FoodDetailsResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.FoodQuery
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.NutritionixResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface NutritionixApi {
    @GET("search/instant")
    fun searchFood(
        @Query("query") query: String,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Call<NutritionixResponse>

    @POST("natural/nutrients")
    @Headers("Content-Type: application/json")
    fun getFoodDetails(
        @Body body: FoodQuery,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Call<FoodDetailsResponse>

}
