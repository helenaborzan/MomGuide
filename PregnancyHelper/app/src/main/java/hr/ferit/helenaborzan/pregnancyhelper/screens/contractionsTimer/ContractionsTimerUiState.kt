package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.os.Build
import androidx.annotation.RequiresApi
import hr.ferit.helenaborzan.pregnancyhelper.model.data.contractions.ContractionsInfo
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
data class ContractionsTimerUiState(
    val contractions : List<ContractionsInfo> = listOf<ContractionsInfo>(),
    val isTimerRunning : Boolean = false,
    val frequencies: List<Duration> = emptyList(),
    val averageContractionDuration : Duration = Duration.ZERO,
    val averageContractionFrequency : Duration = Duration.ZERO

)
