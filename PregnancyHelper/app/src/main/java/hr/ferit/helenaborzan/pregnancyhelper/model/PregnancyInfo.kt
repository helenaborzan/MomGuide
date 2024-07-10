package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.type.DateTime

data class PregnancyInfo(
    var id : String = "",
    val userId : String = "",
    val nutritionInfo : MutableList<NutritionInfo> = mutableListOf<NutritionInfo>(),
    val contractionsInfo : MutableList<ContractionsInfo> = mutableListOf<ContractionsInfo>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
