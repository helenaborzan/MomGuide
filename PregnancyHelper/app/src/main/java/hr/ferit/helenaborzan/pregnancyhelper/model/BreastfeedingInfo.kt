package hr.ferit.helenaborzan.pregnancyhelper.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getHoursAndMins
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
data class BreastfeedingInfo(
    val startTime : Any = "",
    val endTime: Any = "",
    val breast : String = ""
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinutesDifference(): Int? {
        val startTime = getHoursAndMins(startTime)
        val endTime = getHoursAndMins(endTime)

        if (startTime == null || endTime == null) {
            return null
        }

        val minutes1 = startTime["hours"]!! * 60 + startTime["minutes"]!!
        val minutes2 = endTime["hours"]!! * 60 + endTime["minutes"]!!

        return (minutes2 - minutes1)
    }
}
