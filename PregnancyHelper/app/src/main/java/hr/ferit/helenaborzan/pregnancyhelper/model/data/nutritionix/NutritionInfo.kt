package hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix

import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.FoodInfo

data class NutritionInfo(
    val date: Timestamp = Timestamp.now(),
    val foodInfo: List<FoodInfo> = listOf()
)

