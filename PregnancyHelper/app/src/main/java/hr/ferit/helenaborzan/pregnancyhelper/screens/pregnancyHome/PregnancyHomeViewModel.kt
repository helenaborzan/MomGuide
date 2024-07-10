package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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
}