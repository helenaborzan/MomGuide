package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

@RequiresApi(Build.VERSION_CODES.O)
fun formatStartTime(instant: Instant): String {
    return DateTimeFormatter
        .ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(instant)
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertInstantToTemporal(instant : Instant) : Temporal {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}