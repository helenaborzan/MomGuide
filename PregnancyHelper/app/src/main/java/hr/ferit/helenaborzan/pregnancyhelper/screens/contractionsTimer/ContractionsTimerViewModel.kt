package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.graphics.Insets.add
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.screens.registration.RegistrationUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class ContractionsTimerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ContractionsTimerUiState())
    val uiState: StateFlow<ContractionsTimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var lastStartTime: Instant? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun onContractionsButtonClick() {
        val currentTime = Instant.now()
        if (_uiState.value.isTimerRunning) {
            stopTimer()
        } else {
            startTimer()
            addNewContraction(currentTime)
        }
        _uiState.update { it.copy(isTimerRunning = !it.isTimerRunning) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewContraction(startTime: Instant) {
        val newContraction = ContractionsInfo(
            startTime = startTime,
            frequency = if (lastStartTime != null) Duration.between(lastStartTime, startTime) else Duration.ZERO
        )
        _uiState.update {
            it.copy(contractions = it.contractions + newContraction)
        }
        lastStartTime = startTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startTimer() {
        if (timerJob == null) {
            timerJob = viewModelScope.launch {
                while (isActive) {
                    updateDurations()
                    delay(1000) // Ažuriraj svake sekunde
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDurations() {
        val currentTime = Instant.now()
        val updatedContractions = _uiState.value.contractions.mapIndexed { index, contraction ->
            contraction.copy(
                duration = Duration.between(contraction.startTime, currentTime),
                frequency = if (index > 0) {
                    Duration.between(
                        _uiState.value.contractions[index - 1].startTime,
                        contraction.startTime
                    )
                } else {
                    contraction.frequency
                }
            )
        }
        _uiState.update { it.copy(contractions = updatedContractions) }
    }
}