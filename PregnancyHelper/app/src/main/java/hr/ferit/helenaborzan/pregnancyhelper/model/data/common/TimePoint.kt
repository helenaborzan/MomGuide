package hr.ferit.helenaborzan.pregnancyhelper.model.data.common

data class TimePoint(
    val hours: Int,
    val minutes: Int
){
    fun toFloat(): Float = hours + minutes / 60f
}
