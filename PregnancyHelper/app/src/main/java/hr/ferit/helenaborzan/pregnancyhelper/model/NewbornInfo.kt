package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.type.DateTime

data class NewbornInfo(
    var id : String = "",
    val userId : String = "",
    val breastfeedingInfo : MutableList<DateTime> = mutableListOf<DateTime>(),
    val growthAndDevelopmentResults : MutableList<GrowthAndDevelopmentResult> = mutableListOf<GrowthAndDevelopmentResult>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
