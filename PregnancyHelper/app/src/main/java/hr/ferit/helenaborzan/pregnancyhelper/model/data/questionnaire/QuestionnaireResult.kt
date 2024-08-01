package hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire

import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QandA
import java.util.UUID


data class QuestionnaireResult(
    val date : Timestamp? = null,
    val score : Int? = null,
    val resultMessage : String = "",
    val results : List<QandA>? = listOf<QandA>()
)
