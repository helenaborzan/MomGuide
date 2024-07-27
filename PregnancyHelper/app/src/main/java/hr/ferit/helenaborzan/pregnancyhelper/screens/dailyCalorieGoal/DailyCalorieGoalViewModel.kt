package hr.ferit.helenaborzan.pregnancyhelper.screens.dailyCalorieGoal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.calculateTDEE
import hr.ferit.helenaborzan.pregnancyhelper.model.ActivityLevel
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationUiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyCalorieGoalViewModel @Inject constructor(
    private val pregnancyInfoRepository: PregnancyInfoRepository
) : ViewModel() {

    var uiState = mutableStateOf(DailyCalorieGoalUiState())
        private set

    fun onActivityChange(newActivityLevel: ActivityLevel){
        uiState.value = uiState.value.copy(activityLevel = newActivityLevel)
    }

    fun onHeightChange(newHeight : String){
        uiState.value = uiState.value.copy(height = newHeight)
    }

    fun onWeightChange(newWeight : String){
        uiState.value = uiState.value.copy(weight = newWeight)
    }

    fun onAgeChange(newAge : String){
        uiState.value = uiState.value.copy(age = newAge)
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }

    fun isHeightInLimits(height : String) : Boolean{
        if (height.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        }
        else if(height.toDoubleOrNull() != null ){
            return true
        }
        return false
    }

    fun isWeightInLimits(weight : String) : Boolean{
        if (weight.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        }
        else if(weight.toDoubleOrNull() != null ){
            return true
        }
        return false
    }

    fun isAgeInLimits(age : String) : Boolean{
        if (age.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        }
        else if(age.toDoubleOrNull() != null ){
            return true
        }
        return false
    }

    fun isInputValid() : Boolean{
        return isHeightInLimits(uiState.value.height)
                && isWeightInLimits(uiState.value.weight)
                && isAgeInLimits(uiState.value.age)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onCalculateCalorieGoalClick(){
        if(isInputValid()) {
            val calorieGoal = calculateTDEE(
                heightCm = uiState.value.height.toDouble(),
                weightKg = uiState.value.weight.toDouble(),
                age = uiState.value.age.toInt(),
                activityLevel = uiState.value.activityLevel
            )
            viewModelScope.launch {
                pregnancyInfoRepository.updateDailyCalorieGoal(dailyCalorieGoal = calorieGoal)
            }
        }
    }


}