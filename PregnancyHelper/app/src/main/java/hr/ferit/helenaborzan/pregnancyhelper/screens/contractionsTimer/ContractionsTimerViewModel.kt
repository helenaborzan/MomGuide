package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.graphics.Insets.add
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.convertInstantToTemporal
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.convertTimestampToTemporal
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
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import javax.inject.Inject


@HiltViewModel
class ContractionsTimerViewModel @Inject constructor() : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(ContractionsTimerUiState())

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ContractionsTimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun onContractionsButtonClick() {
        val currentTime = Instant.now()
        if (_uiState.value.isTimerRunning) {
            endContraction(currentTime)
        } else {
            startNewContraction(currentTime)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startNewContraction(startTime: Instant) {
        val newContraction = ContractionsInfo(startTime = startTime)
        val newFrequency = if (_uiState.value.contractions.isNotEmpty()) {
            Duration.between(_uiState.value.contractions.last().startTime, startTime)
        } else null

        _uiState.update {
            it.copy(
                isTimerRunning = true,
                contractions = it.contractions + newContraction,
                frequencies = if (newFrequency != null) it.frequencies + newFrequency else it.frequencies
            )
        }

        startTimer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endContraction(endTime: Instant) {
        stopTimer()

        val updatedContractions = _uiState.value.contractions.toMutableList()
        if (updatedContractions.isNotEmpty()) {
            val lastContraction = updatedContractions.last()
            updatedContractions[updatedContractions.lastIndex] = lastContraction.copy(
                endTime = endTime,
                duration = Duration.between(lastContraction.startTime, endTime)
            )
        }

        _uiState.update {
            it.copy(
                isTimerRunning = false,
                contractions = updatedContractions
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                updateCurrentContractionDuration()
                delay(1000) // Ažuriraj svake sekunde
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCurrentContractionDuration() {
        val currentTime = Instant.now()
        val updatedContractions = _uiState.value.contractions.toMutableList()
        if (updatedContractions.isNotEmpty()) {
            val lastContraction = updatedContractions.last()
            updatedContractions[updatedContractions.lastIndex] = lastContraction.copy(
                duration = Duration.between(lastContraction.startTime, currentTime)
            )
        }

        _uiState.update { it.copy(contractions = updatedContractions) }
        updateAverages()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAverages() {
        val completedContractions = _uiState.value.contractions.filter { it.endTime != null }

        val averageDuration = if (completedContractions.isNotEmpty()) {
            completedContractions.map { it.duration }.reduce { acc, duration -> acc.plus(duration) }
                .dividedBy(completedContractions.size.toLong())
        } else {
            Duration.ZERO
        }

        val averageFrequency = if (_uiState.value.frequencies.size > 1) {
            _uiState.value.frequencies.reduce { acc, duration -> acc.plus(duration) }
                .dividedBy(_uiState.value.frequencies.size.toLong())
        } else {
            Duration.ZERO
        }

        _uiState.update {
            it.copy(
                averageContractionDuration = averageDuration,
                averageContractionFrequency = averageFrequency
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun shouldGoToTheHospital(uiState: ContractionsTimerUiState): Boolean {
        val currentDuration = if (uiState.contractions.isEmpty()) Duration.ZERO
        else Duration.between(convertInstantToTemporal(uiState.contractions[0].startTime), convertTimestampToTemporal(Timestamp.now()))

        Log.e("Contractions", "Current Duration: ${currentDuration.seconds}")

        if (uiState.averageContractionDuration == Duration.ZERO || uiState.averageContractionFrequency == Duration.ZERO) {
            return false
        }

        return (currentDuration.seconds >= 60)
                && (uiState.averageContractionDuration.seconds in 5..10)
                && (uiState.averageContractionFrequency.seconds in 10..15)
    }
}
