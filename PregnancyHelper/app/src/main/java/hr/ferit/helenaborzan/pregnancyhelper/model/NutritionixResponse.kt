package hr.ferit.helenaborzan.pregnancyhelper.model

data class NutritionixResponse(
    val common: List<FoodItem> = emptyList(),
    val branded: List<FoodItem> = emptyList()
)
