package hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment

import com.google.firebase.Timestamp

data class GrowthAndDevelopmentInfo(
    val date : Timestamp? = null,
    val sex : String = "",
    val length : String = "",
    val weight : String = "",
    val age : String = "",
    val headCircumference : String = ""
)
