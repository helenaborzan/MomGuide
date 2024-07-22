package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class BreastfeedingInfoUiState(
    val selectedDate : LocalDate = LocalDate.now(),
    val feedingType : String = "Dojenje"
)
