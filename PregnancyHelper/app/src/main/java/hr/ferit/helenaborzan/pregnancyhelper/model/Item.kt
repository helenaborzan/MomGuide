package hr.ferit.helenaborzan.pregnancyhelper.model

data class Item(
    val item_name: String,
    val brand_name: String,
    val thumbnail: String,
    val nutrient_name: String,
    val nutrient_value: Float,
    val nutrient_uom: String,
    val serving_qty: Int,
    val serving_uom: String,
    val resource_id: String?,
    val nutrients: List<Nutrient>?
)
