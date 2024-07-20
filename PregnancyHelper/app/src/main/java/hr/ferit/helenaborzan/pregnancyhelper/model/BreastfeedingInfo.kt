package hr.ferit.helenaborzan.pregnancyhelper.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
data class BreastfeedingInfo(
    val feedingType : String = "",
    val startTime : Any = "",
    val endTime: Any = "",
    val breast : String = ""
)
