package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.type.DateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class NewbornHomeViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository,
    private val accountService: AccountService
) : ViewModel(){

    private val _newbornInfo = MutableStateFlow<List<NewbornInfo>>(emptyList())
    val newbornInfo: StateFlow<List<NewbornInfo>> = _newbornInfo.asStateFlow()

    var uiState = mutableStateOf(NewbornHomeUiState())
        private set

    private val _showDialog = mutableStateOf(false)
    val showDialog : State<Boolean> = _showDialog

    fun getUsersNewbornInfo() {
        viewModelScope.launch {
            newbornInfoRepository.getUsersNewbornInfo()
                .catch { exception ->
                    Log.e("NewbornHomeViewModel", "Error fetching Newborn Info", exception)
                }
                .collect { newbornInfo ->
                    _newbornInfo.value = newbornInfo
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date : DateTime){
        val localDateTime = LocalDateTime.parse("2018-12-14T09:55:00")
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val output = formatter.format(localDateTime)
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

    fun onDeleteResultClick(){
        _showDialog.value = !_showDialog.value
    }

    fun deletePercentileResult(growthAndDevelopmentResult: GrowthAndDevelopmentResult){
        viewModelScope.launch {
            newbornInfoRepository.deletePercentileResult(growthAndDevelopmentResult)
        }
    }
    fun onDeleteResultDialogDismiss(){
        _showDialog.value = false
    }
}