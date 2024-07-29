package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.convertToTimestamp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class BreastfeedingViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository
) : ViewModel(){
    @RequiresApi(Build.VERSION_CODES.O)
    var uiState = mutableStateOf(BreastfeedingInputUiState())
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
    fun onTimeChange(newTime : LocalTime){
        uiState.value = uiState.value.copy(time = newTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onQuantityChange(newQuantity : String){
        uiState.value = uiState.value.copy(quantity = newQuantity)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun areAllBreastfeedingFieldsChecked() : Boolean{
        return isBreastFieldChecked()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun areAllBottleFieldsChecked() : Boolean{
        return isQuantityFieldChecked()
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
    fun isQuantityFieldChecked() : Boolean{
        if (uiState.value.quantity.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        }
        else if(uiState.value.quantity.toIntOrNull() == null){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.notIntegerError)
            return false
        }
        else if (uiState.value.quantity.toInt() <= 0){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.milkQuantityNotInLimits)
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
        if (uiState.value.feedingType == "Dojenje"){
            onSubmitBreastfeedingClick()
        }
        else onSubmitBottleClick()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitBreastfeedingClick(){
        if(areAllBreastfeedingFieldsChecked()) {

            val feedingType = uiState.value.feedingType
            val startTime = convertToTimestamp(uiState.value.startTime)
            val endTime = convertToTimestamp(uiState.value.endTime)
            val breast = uiState.value.breast
            var breastfeedingInfo = BreastfeedingInfo(
                startTime = startTime,
                endTime = endTime,
                breast = breast
            )
            viewModelScope.launch {
                if (breastfeedingInfo != null) {
                    newbornInfoRepository.addBreastfeedingInfo(
                        breastfeedingInfo = breastfeedingInfo
                    )
                    uiState.value = uiState.value.copy(isAddingSuccesful = true)
                }
            }
        }
        else{
            uiState.value = uiState.value.copy(isAddingSuccesful = false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitBottleClick(){
        if(areAllBottleFieldsChecked()) {

            val feedingType = uiState.value.feedingType
            val quantity = uiState.value.quantity.toInt()
            val time = convertToTimestamp(uiState.value.time)
            var bottleInfo = BottleInfo(
                time = time,
                quantity = quantity
            )
            viewModelScope.launch {
                if (bottleInfo != null) {
                    newbornInfoRepository.addBottleInfo(
                        bottleInfo = bottleInfo
                    )
                    uiState.value = uiState.value.copy(isAddingSuccesful = true)
                }
            }
        }
        else{
            uiState.value = uiState.value.copy(isAddingSuccesful = true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }
}

