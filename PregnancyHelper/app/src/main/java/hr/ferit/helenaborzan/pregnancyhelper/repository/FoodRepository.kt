package hr.ferit.helenaborzan.pregnancyhelper.repository

import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.model.service.NutritionixApi
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import retrofit2.Callback

class FoodRepository @Inject constructor(
    private val nutritionixApi: NutritionixApi
) {
    fun searchFood(query: String, callback: (NutritionixResponse?) -> Unit) {
        val call = nutritionixApi.searchFood(
            fields = "item_name,brand_name,nf_calories,nf_total_fat",
            appId = "YOUR_APP_ID",
            appKey = "YOUR_APP_KEY",
            query = query
        )

        call.enqueue(object : Callback<NutritionixResponse> {
            override fun onResponse(
                call: Call<NutritionixResponse>,
                response: Response<NutritionixResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<NutritionixResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}