package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.convertInstantToTemporal
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.convertTimestampToTemporal
import hr.ferit.helenaborzan.pregnancyhelper.model.data.contractions.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Red
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


@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class ContractionsTimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(ContractionsTimerUiState())

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ContractionsTimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var notificationJob: Job? = null
    @RequiresApi(Build.VERSION_CODES.O)
    fun onContractionsButtonClick() {
        val currentTime = Instant.now()
        if (_uiState.value.isTimerRunning) {
            endContraction(currentTime)
        } else {
            startNewContraction(currentTime)
        }
    }
    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "contractions_channel_id"
            val channelName = "Contractions Notifications"
            val channelDescription = "Notifications for contractions monitoring"

            // Create the NotificationChannel with the specified channelId
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelDescription
                enableLights(true)
                enableVibration(true)
                lightColor = Red.toArgb()
                vibrationPattern = longArrayOf(1000, 500, 1000)
            }

            // Get the NotificationManager
            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)

            // Create the notification channel
            notificationManager?.createNotificationChannel(channel)
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
        startNotificationCheck()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endContraction(endTime: Instant) {
        stopTimer()
        notificationJob?.cancel()

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
    @RequiresApi(Build.VERSION_CODES.S)
    fun shouldPromptForNotifications(uiState: ContractionsTimerUiState): Boolean {
        return !areNotificationsEnabled()
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

        return (currentDuration.toMinutes() >= 60)
                && (uiState.averageContractionDuration.seconds in 45..75)
                && (uiState.averageContractionFrequency.toMinutes() in 4..6)
    }

    private fun showNotification() {
        val notificationId = 1
        val channelId = "contractions_channel_id"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_local_hospital_24)
            .setContentTitle("Contractions Alert")
            .setContentText("You may need to go to the hospital.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startNotificationCheck() {
        notificationJob = viewModelScope.launch {
            while (isActive) {
                if (areNotificationsEnabled()) {
                    checkAndNotify()
                }
                delay(60000)
            }
        }
    }
    private suspend fun checkAndNotify() {
        if (shouldGoToTheHospital(uiState.value)) {
            showNotification()
        }
    }



    private fun areNotificationsEnabled(): Boolean {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        return notificationManager?.areNotificationsEnabled() ?: false
    }


}
