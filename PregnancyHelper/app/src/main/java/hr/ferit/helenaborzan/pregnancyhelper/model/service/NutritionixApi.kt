package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.FoodDetailsResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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
    fun getFoodDetails(
        @Body query: Map<String, String>,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Call<FoodDetailsResponse>

}