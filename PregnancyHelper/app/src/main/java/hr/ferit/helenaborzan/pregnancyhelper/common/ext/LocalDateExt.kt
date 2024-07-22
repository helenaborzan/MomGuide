package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
fun anyToLocalDate(any: Any): LocalDate? {
    return when (any) {
        is Timestamp -> Instant.ofEpochSecond(any.seconds).atZone(ZoneId.systemDefault()).toLocalDate()
        is Date -> any.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        is Long -> Instant.ofEpochMilli(any).atZone(ZoneId.systemDefault()).toLocalDate()
        is LocalDate -> any
        else -> null
    }
}