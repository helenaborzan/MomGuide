package hr.ferit.helenaborzan.pregnancyhelper.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.type.DateTime
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
data class PregnancyInfo(
    var id : String = "",
    val pregnancyStartDate : LocalDate = LocalDate.now(),
    val userId : String = "",
    val nutritionInfo : MutableList<NutritionInfo> = mutableListOf<NutritionInfo>(),
    val contractionsInfo : MutableList<ContractionsInfo> = mutableListOf<ContractionsInfo>(),
    val questionnaireResults : MutableList<QuestionnaireResult> = mutableListOf<QuestionnaireResult>()
)
