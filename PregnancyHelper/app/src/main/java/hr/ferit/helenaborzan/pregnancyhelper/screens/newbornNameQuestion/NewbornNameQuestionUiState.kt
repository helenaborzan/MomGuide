package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornNameQuestion

import androidx.annotation.StringRes
import com.google.firebase.Timestamp

data class NewbornNameQuestionUiState(
    val name : String = "",
    val sex : String = "",
    @StringRes val errorMessageResource : Int? = null
)
