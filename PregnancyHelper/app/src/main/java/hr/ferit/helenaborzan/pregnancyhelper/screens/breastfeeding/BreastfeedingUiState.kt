package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.google.firebase.Timestamp
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
data class BreastfeedingUiState constructor(
    val feedingType : String = "Dojenje",
    val startTime : LocalTime = LocalTime.now(),
    val endTime: LocalTime = LocalTime.now(),
    val breast : String = "",
    val time : LocalTime = LocalTime.now(),
    val quantity : String = "",
    @StringRes val errorMessageResource : Int? = null
)
