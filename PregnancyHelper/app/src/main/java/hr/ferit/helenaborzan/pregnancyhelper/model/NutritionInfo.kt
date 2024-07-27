package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.firebase.Timestamp
import java.util.Date

data class NutritionInfo(
    val date: Timestamp = Timestamp.now(),
    val foodName : String = "",
    val calories : Double? = null,
    val totalFat : Double? = null,
    val carbohydrate : Double? = null,
    val protein : Double? = null,
    val dietaryFiber : Double? = null,
    val sugars : Double? = null,
    val sodium : Double? = null,
    val servingQuantity : Double? = null,
    val servingUnit : String? = null,
    val servingWeight : Int? = null
)
