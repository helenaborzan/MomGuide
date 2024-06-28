package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import com.google.firebase.Timestamp
import java.util.Calendar


fun getDate(timestamp: Timestamp) : Map<String, Int>{
    val date = timestamp.toDate()
    val calendar = Calendar.getInstance()
    calendar.time = date
    val dateYMD = hashMapOf(
        "year" to calendar.get(Calendar.YEAR),
        "month" to calendar.get(Calendar.MONTH)+1,
        "day" to calendar.get(Calendar.DAY_OF_MONTH)
    )
    return dateYMD
}