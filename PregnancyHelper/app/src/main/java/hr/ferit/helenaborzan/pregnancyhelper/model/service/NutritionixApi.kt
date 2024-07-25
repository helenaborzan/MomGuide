package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NutritionixApi {
    @GET("search/instant")
    fun searchFood(
        @Query("query") query: String,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Call<NutritionixResponse>
}