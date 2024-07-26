package hr.ferit.helenaborzan.pregnancyhelper.model

data class NutritionixResponse(
    val common : List<Food> = emptyList(),
    val branded : List<Food> = emptyList()
)