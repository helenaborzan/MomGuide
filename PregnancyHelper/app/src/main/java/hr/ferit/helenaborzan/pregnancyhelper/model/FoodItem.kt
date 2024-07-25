package hr.ferit.helenaborzan.pregnancyhelper.model

data class FoodItem(
    val food_name: String,
    val serving_unit: String? = null,
    val nix_item_id: String? = null,
    val brand_name: String? = null,
    val serving_qty: Double? = null,
    val nf_calories: Double? = null,
    val photo: Photo? = null,
    val brand_name_item_name: String? = null,
    val region: Int? = null,
    val nix_brand_id: String? = null,
    val locale: String? = null
)
