package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.firebase.Timestamp
import com.google.type.Date
import com.google.type.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


data class QuestionnaireResult(
    val date : Timestamp? = null,
    val score : Int? = null,
    val resultMessage : String = ""
)
