package hr.ferit.helenaborzan.pregnancyhelper.model.service

import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EdamamApi {
    @GET("api/recipes/v2")
    fun getRecipes(
        @Query("type") type: String = "public",
        @Query("q") query: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("health") health: Array<String> = arrayOf("alcohol-free"),
        @Query("to") to: Int = 15
    ): Call<RecipeResponse>
}