package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionixApi {
    @GET("v2_0/search")
    fun searchFood(
        @Query("fields") fields: String,
        @Query("appId") appId: String,
        @Query("appKey") appKey: String,
        @Query("query") query: String
    ): Call<NutritionixResponse>
}