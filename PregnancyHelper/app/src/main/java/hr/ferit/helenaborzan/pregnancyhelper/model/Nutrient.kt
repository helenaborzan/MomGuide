package hr.ferit.helenaborzan.pregnancyhelper.model

data class Nutrient(
    val attr_id: Int,
    val value: Float,
    val unit: String,
    val name: String,
    val usda_tag: String
)
