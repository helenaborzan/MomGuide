package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@HiltViewModel
class PregnancyHomeViewModel @Inject constructor(
    private val pregnancyInfoRepository: PregnancyInfoRepository,
    private val accountService: AccountService
) : ViewModel(){
    private val _pregnancyInfo = MutableStateFlow<List<PregnancyInfo>>(emptyList())
    val pregnancyInfo: StateFlow<List<PregnancyInfo>> = _pregnancyInfo.asStateFlow()
    var uiState = mutableStateOf(PregnancyHomeUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsersPregnancyInfo() {
        viewModelScope.launch {
            pregnancyInfoRepository.getUsersPregnancyInfo()
                .catch { exception ->
                    Log.e("PregnancyHomeViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _pregnancyInfo.value = pregnancyInfo
                }
        }
    }
    fun onSignOutClick(){
        viewModelScope.launch {
            try {
                accountService.signOut()
                uiState.value = uiState.value.copy(isSignedOut = true)
            } catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = R.string.signOutError, isSignedOut = false)
            }
        }
    }
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeksPregnant(pregnancyStartTime : Timestamp) : Int{
        val startInstant: Instant = pregnancyStartTime.toInstant()
        val nowInstant: Instant = Instant.now()
        val daysPregnant = ChronoUnit.DAYS.between(startInstant, nowInstant)
        return (daysPregnant / 7).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrimester(pregnancyStartTime: Timestamp): Int {
        val weeksPregnant = getWeeksPregnant(pregnancyStartTime)
        return when (weeksPregnant) {
            in 1..12 -> 1
            in 13..26 -> 2
            in 27..45 -> 3
            else -> -1
        }
    }

}