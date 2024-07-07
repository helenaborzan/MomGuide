package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.firebase.Timestamp

data class GrowthAndDevelopmentInfo(
    val date : Timestamp? = null,
    val sex : String = "",
    val length : Int = 0,
    val weight : Int = 0,
    val age : Int = 0,
    val headCircumference : Int = 0
)
