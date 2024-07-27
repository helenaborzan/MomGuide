package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.firebase.Timestamp
import java.util.Date

data class NutritionInfo(
    val date: Timestamp = Timestamp.now(),
    val foodInfo: List<FoodInfo> = listOf()
)

