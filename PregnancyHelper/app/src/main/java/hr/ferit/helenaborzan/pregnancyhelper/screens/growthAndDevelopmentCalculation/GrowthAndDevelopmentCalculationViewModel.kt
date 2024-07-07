package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopmentCalculation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.PercentileCalculator
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrowthAndDevelopmentCalculationViewModel @Inject constructor(
    private val percentileCalculator: PercentileCalculator,
    private val newbornInfoRepository: NewbornInfoRepository
): ViewModel(){
    var uiState = mutableStateOf(GrowthAndDevelopmentCalculationUiState())
        private set
    private val _showResults = mutableStateOf(false)
    val showResults: State<Boolean> = _showResults

    fun onSexChange(newValue : String){
        uiState.value = uiState.value.copy(growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(sex = newValue))
    }

    fun onHeightChange(newValue : String){
        var newValue = newValue.toIntOrNull() ?: 0
        uiState.value = uiState.value.copy(growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(length = newValue))
    }

    fun onWeightChange(newValue : String){
        var newValue = newValue.toIntOrNull() ?: 0
        uiState.value = uiState.value.copy(growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(weight = newValue))
    }

    fun onAgeChange(newValue : String){
        var newValue = newValue.toIntOrNull() ?: 0
        uiState.value = uiState.value.copy(growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(age = newValue))
    }

    fun onHeadCircumferenceChange(newValue: String){
        var newValue = newValue.toIntOrNull() ?: 0
        uiState.value = uiState.value.copy(growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(headCircumference = newValue))
    }

    fun onCalculatePercentilesClick(){
        calculatePercentiles()

        val growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo
        val growthAndDevelopmentPercentiles = uiState.value.growthAndDevelopmentPercentiles

        viewModelScope.launch {
            if(growthAndDevelopmentInfo!=null && growthAndDevelopmentPercentiles!=null) {
                newbornInfoRepository.addGrowthAndDevelopmentResult(
                    growthAndDevelopmentInfo = growthAndDevelopmentInfo,
                    growthAndDevelopmentPercentiles = growthAndDevelopmentPercentiles
                )
            }
        }
        _showResults.value = true
    }

    fun calculatePercentiles(){
       uiState.value = uiState.value.copy(
           growthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles(
           lengthForAgePercentile = percentileCalculator.calculatePercentile(type = "length_age", sex = uiState.value.growthAndDevelopmentInfo!!.sex, searchCriteria = uiState.value.growthAndDevelopmentInfo!!.age, value = uiState.value.growthAndDevelopmentInfo!!.length),
           weightForAgePercentile = percentileCalculator.calculatePercentile(type = "weight_age", sex = uiState.value.growthAndDevelopmentInfo!!.sex, searchCriteria = uiState.value.growthAndDevelopmentInfo!!.age, value = uiState.value.growthAndDevelopmentInfo!!.weight),
           weightForLengthPercentile = percentileCalculator.calculatePercentile(type = "weight_length", sex = uiState.value.growthAndDevelopmentInfo!!.sex, searchCriteria = uiState.value.growthAndDevelopmentInfo!!.length, value = uiState.value.growthAndDevelopmentInfo!!.weight),
           headCircumferenceForAgePercentile = percentileCalculator.calculatePercentile(type = "head_circumference_for_age", sex = uiState.value.growthAndDevelopmentInfo!!.sex, searchCriteria = uiState.value.growthAndDevelopmentInfo!!.age, value = uiState.value.growthAndDevelopmentInfo!!.headCircumference)
            )
       )
    }

    fun isPercentileInNormalLimits(percentileValue: Double) : Boolean{
        return percentileCalculator.isPercentileInNormalLimits(percentileValue)
    }

    fun getPercentileInterpretationResource(percentileValue: Double) : Int{
        if(isPercentileInNormalLimits(percentileValue)){
            return R.string.normalPercentileValue
        }
        return R.string.abnormalPercentilaValue
    }
}