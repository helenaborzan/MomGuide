package hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam

data class Digest(
    val daily: Double,
    val hasRDI: Boolean,
    val label: String,
    val schemaOrgTag: String,
    val sub: List<DigestSub>,
    val tag: String,
    val total: Double,
    val unit: String
)

data class DigestSub(
    val label: String,
    val tag: String,
    val schemaOrgTag: String?,
    val total: Double,
    val hasRDI: Boolean,
    val daily: Double,
    val unit: String
)