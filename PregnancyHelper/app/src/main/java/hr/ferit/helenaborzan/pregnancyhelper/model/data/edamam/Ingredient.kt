package hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam

data class Ingredient(
    val food: String,
    val foodId: String,
    val measure: String,
    val quantity: Double,
    val text: String,
    val weight: Double
)