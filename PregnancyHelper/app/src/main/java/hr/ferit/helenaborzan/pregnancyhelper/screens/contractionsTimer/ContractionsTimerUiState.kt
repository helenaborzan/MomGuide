package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import androidx.compose.runtime.MutableState
import hr.ferit.helenaborzan.pregnancyhelper.model.ContractionsInfo

data class ContractionsTimerUiState(
    val contractions : List<ContractionsInfo> = listOf<ContractionsInfo>(),
    val isTimerRunning : Boolean = false

)
