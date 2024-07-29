package hr.ferit.helenaborzan.pregnancyhelper.model.data.contractions

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
data class ContractionsInfo(
    val startTime : Instant,
    var endTime : Instant? = null,
    var duration : Duration = Duration.ZERO
)
