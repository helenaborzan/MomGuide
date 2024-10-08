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
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.util.Calendar
import java.util.Date


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

@RequiresApi(Build.VERSION_CODES.O)
fun getDate(input: Any): Map<String, Int> {
    val calendar = Calendar.getInstance()

    when (input) {
        is Timestamp -> calendar.time = input.toDate()
        is Date -> calendar.time = input
        is Long -> calendar.timeInMillis = input
        is LocalDate -> {
            calendar.set(input.year, input.monthValue - 1, input.dayOfMonth)
        }
        else -> throw IllegalArgumentException("Nepoznati tip ulaznog parametra: ${input::class.java}")
    }

    return mapOf(
        "year" to calendar.get(Calendar.YEAR),
        "month" to calendar.get(Calendar.MONTH) + 1,
        "day" to calendar.get(Calendar.DAY_OF_MONTH)
    )
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
fun getHoursAndMins(value: Any): Map<String, Int>? {
    return when (value) {
        is Timestamp -> {
            val date = value.toDate()
            val calendar = Calendar.getInstance()
            calendar.time = date
            mapOf(
                "hours" to calendar.get(Calendar.HOUR_OF_DAY),
                "minutes" to calendar.get(Calendar.MINUTE)
            )
        }
        is String -> {
            try {
                val instant = Instant.parse(value)
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                mapOf(
                    "hours" to localDateTime.hour,
                    "minutes" to localDateTime.minute
                )
            } catch (e: DateTimeParseException) {
                null // If parsing fails, return null
            }
        }
        is LocalTime -> {
            mapOf(
                "hours" to value.hour,
                "minutes" to value.minute
            )
        }
        is LocalDateTime -> {
            mapOf(
                "hours" to value.hour,
                "minutes" to value.minute
            )
        }
        is Date -> {
            val calendar = Calendar.getInstance()
            calendar.time = value
            mapOf(
                "hours" to calendar.get(Calendar.HOUR_OF_DAY),
                "minutes" to calendar.get(Calendar.MINUTE)
            )
        }
        is Long -> { // If the value is a long timestamp (milliseconds)
            val date = Date(value)
            val calendar = Calendar.getInstance()
            calendar.time = date
            mapOf(
                "hours" to calendar.get(Calendar.HOUR_OF_DAY),
                "minutes" to calendar.get(Calendar.MINUTE)
            )
        }
        else -> null // If the type is not supported, return null
    }
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

@RequiresApi(Build.VERSION_CODES.O)
fun getString(date : Any) : String{
    val year = getDate(date).get("year")
    val month = getDate(date).get("month")
    val day = getDate(date).get("day")

    return "$day.$month.$year."
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertTimestampToTemporal(timestamp: Timestamp): Temporal {
    val instant = timestamp.toInstant()

    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}

