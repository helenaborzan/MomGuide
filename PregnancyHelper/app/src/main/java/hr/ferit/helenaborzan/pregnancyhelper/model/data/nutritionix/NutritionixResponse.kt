package hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix

data class NutritionixResponse(
    val common : List<Food> = emptyList(),
    val branded : List<Food> = emptyList()
)