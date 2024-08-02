package hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn

import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult

data class NewbornInfo(
    var id : String = "",
    val userId : String = "",
    val name : String? = null,
    val sex : String? = null,
    val breastfeedingInfo : MutableList<BreastfeedingInfo> = mutableListOf<BreastfeedingInfo>(),
    val bottleInfo : MutableList<BottleInfo> = mutableListOf<BottleInfo>(),
    val growthAndDevelopmentResults : MutableList<GrowthAndDevelopmentResult> = mutableListOf<GrowthAndDevelopmentResult>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
