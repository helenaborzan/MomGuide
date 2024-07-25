package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.google.firebase.Timestamp
import java.time.LocalDate


data class PregnancyStartUiState(
    val pregnancyStartDate : Timestamp = Timestamp.now(),
    @StringRes val errorMessageResource : Int? = null
)
