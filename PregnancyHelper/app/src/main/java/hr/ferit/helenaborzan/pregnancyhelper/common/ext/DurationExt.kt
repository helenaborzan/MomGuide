package hr.ferit.helenaborzan.pregnancyhelper.common.ext

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
fun formatDuration(duration: Duration): String {
    val minutes = duration.toMinutes()
    val seconds = duration.minusMinutes(minutes).seconds
    return String.format("%02d:%02d", minutes, seconds)
}
