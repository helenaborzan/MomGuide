package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
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

fun getHoursAndMins(timestamp: Timestamp) : Map<String, Int>{
    val date = timestamp.toDate()
    val calendar = Calendar.getInstance()
    calendar.time = date
    val dateHHMM = hashMapOf(
        "hours" to calendar.get(Calendar.HOUR),
        "minutes" to calendar.get(Calendar.MINUTE)
    )
    return dateHHMM
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertToTimestamp(value: Any): Any {
    return when (value) {
        is Timestamp -> value
        is String -> {
            try {
                val instant = Instant.parse(value)
                Timestamp(instant.epochSecond, instant.nano)
            } catch (e: DateTimeParseException) {
                value // If parsing fails, return the original string
            }
        }
        is LocalTime -> {
            val localDateTime = LocalDateTime.of(LocalDate.now(), value)
            val zoneId = ZoneId.systemDefault()
            val instant = localDateTime.atZone(zoneId).toInstant()
            Timestamp(instant.epochSecond, instant.nano)
        }
        else -> value
    }
}
