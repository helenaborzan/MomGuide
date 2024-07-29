package hr.ferit.helenaborzan.pregnancyhelper.model.data.pregnancy

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.contractions.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.NutritionInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult


@RequiresApi(Build.VERSION_CODES.O)
data class PregnancyInfo(
    var id : String = "",
    val pregnancyStartDate : Timestamp? = null,
    var dailyCalorieGoal : Double? =null,
    val userId : String = "",
    val nutritionInfo : MutableList<NutritionInfo> = mutableListOf<NutritionInfo>(),
    val contractionsInfo : MutableList<ContractionsInfo> = mutableListOf<ContractionsInfo>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
