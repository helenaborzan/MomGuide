package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationUiState
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
import javax.inject.Inject


@HiltViewModel
class BreastfeedingViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository
) : ViewModel(){
    @RequiresApi(Build.VERSION_CODES.O)
    var uiState = mutableStateOf(BreastfeedingUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun onFeedingTypeChange(newFeedingType : String){
        uiState.value = uiState.value.copy(feedingType = newFeedingType)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onStartTimeChange(newStartTime : LocalTime){
        uiState.value = uiState.value.copy(startTime = newStartTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEndTimeChange(newEndTime: LocalTime){
        uiState.value = uiState.value.copy(endTime = newEndTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onBreastChange(newBreast : String){
        uiState.value = uiState.value.copy(breast = newBreast)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun areAllBreastfeedingFieldsChecked() : Boolean{
        return isBreastFieldChecked()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isBreastFieldChecked() : Boolean{
        if(uiState.value.breast.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onWrongTimeInput(){
        uiState.value = uiState.value.copy(errorMessageResource = R.string.wrongTimeInput)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitClick(){
        if(areAllBreastfeedingFieldsChecked()) {

            val feedingType = uiState.value.feedingType
            val startTime = convertToTimestamp(uiState.value.startTime)
            val endTime = convertToTimestamp(uiState.value.endTime)
            val breast = uiState.value.breast
            var breastfeedingInfo = BreastfeedingInfo(
                feedingType = feedingType,
                startTime = startTime,
                endTime = endTime,
                breast = breast
            )
            viewModelScope.launch {
                if (breastfeedingInfo != null) {
                    newbornInfoRepository.addBreastfeedingInfo(
                       breastfeedingInfo = breastfeedingInfo
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToTimestamp(value: Any): Any {
        return when (value) {
            is Timestamp -> value
            is String -> {
                try {
                    val instant = Instant.parse(value)
                    Timestamp(instant.epochSecond, instant.nano)
                } catch (e: DateTimeParseException) {
                    value // If parsing fails, return the original string
                }
            }
            else -> value
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }
}

