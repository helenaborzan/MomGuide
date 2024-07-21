package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.type.DateTime

data class NewbornInfo(
    var id : String = "",
    val userId : String = "",
    val breastfeedingInfo : MutableList<BreastfeedingInfo> = mutableListOf<BreastfeedingInfo>(),
    val bottleInfo : MutableList<BottleInfo> = mutableListOf<BottleInfo>(),
    val growthAndDevelopmentResults : MutableList<GrowthAndDevelopmentResult> = mutableListOf<GrowthAndDevelopmentResult>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
