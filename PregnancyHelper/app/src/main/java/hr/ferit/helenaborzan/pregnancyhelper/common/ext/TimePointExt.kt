package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import android.os.Build
import androidx.annotation.RequiresApi
import hr.ferit.helenaborzan.pregnancyhelper.model.data.common.TimePoint


@RequiresApi(Build.VERSION_CODES.O)
fun timesToTimePoints(times : List<Any>) : MutableList<TimePoint>{
    var timepoints = mutableListOf<TimePoint>()
    for (time in times){
        val hours = getHoursAndMins(time)?.get("hours") ?: 0
        val minutes = getHoursAndMins(time)?.get("minutes") ?: 0
        timepoints.add(TimePoint(hours = hours, minutes = minutes))
    }
    return timepoints
}
