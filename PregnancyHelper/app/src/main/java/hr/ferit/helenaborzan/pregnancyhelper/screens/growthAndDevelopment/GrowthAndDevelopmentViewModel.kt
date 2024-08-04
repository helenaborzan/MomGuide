package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.PercentileCalculator
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.ResourceHelper
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrowthAndDevelopmentViewModel @Inject constructor(
    private val percentileCalculator: PercentileCalculator,
    private val newbornInfoRepository: NewbornInfoRepository,
    private val resourceHelper: ResourceHelper
): ViewModel() {
    var uiState = mutableStateOf(GrowthAndDevelopmentCalculationUiState())
        private set
    private val _showResults = mutableStateOf(false)
    val showResults: State<Boolean> = _showResults

    private val _newbornInfo = MutableStateFlow<List<NewbornInfo>>(emptyList())
    val newbornInfo: StateFlow<List<NewbornInfo>> get() = _newbornInfo

    init {
        getUsersNewbornInfo()
    }
    fun getUsersNewbornInfo() {
        viewModelScope.launch {
            newbornInfoRepository.getUserInfo()
                .catch { exception ->
                    Log.e("NewbornHomeViewModel", "Error fetching Newborn Info", exception)
                }
                .collect { newbornInfo ->
                    _newbornInfo.value = newbornInfo
                }
        }
    }
    fun onHeightChange(newValue: String) {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(length = newValue)
        )

    }

    fun onWeightChange(newValue: String) {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(weight = newValue)
        )
    }

    fun onAgeChange(newValue: String) {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(age = newValue)
        )
    }

    fun onHeadCircumferenceChange(newValue: String) {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(headCircumference = newValue)
        )

    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }

    fun onCalculatePercentilesClick() {
        if (areAllFieldsChecked()) {
            calculatePercentiles()

            var growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo
            val growthAndDevelopmentPercentiles = uiState.value.growthAndDevelopmentPercentiles
            growthAndDevelopmentInfo = growthAndDevelopmentInfo.copy(date = Timestamp.now())
            viewModelScope.launch {
                if (growthAndDevelopmentInfo != null && growthAndDevelopmentPercentiles != null) {
                    newbornInfoRepository.addGrowthAndDevelopmentResult(
                        growthAndDevelopmentInfo = growthAndDevelopmentInfo,
                        growthAndDevelopmentPercentiles = growthAndDevelopmentPercentiles
                    )
                }
            }
            _showResults.value = true
        }
    }

    fun calculatePercentiles() {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles(
                lengthForAgePercentile = percentileCalculator.calculatePercentile(
                    type = "length_age",
                    sex =  newbornInfo.value.firstOrNull()?.sex ?: "male",
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.age.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.length.toInt()
                ),
                weightForAgePercentile = percentileCalculator.calculatePercentile(
                    type = "weight_age",
                    sex = newbornInfo.value.firstOrNull()?.sex ?: "male",
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.age.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.weight.toInt()
                ),
                weightForLengthPercentile = percentileCalculator.calculatePercentile(
                    type = "weight_length",
                    sex = newbornInfo.value.firstOrNull()?.sex ?: "male",
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.length.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.weight.toInt()
                ),
                headCircumferenceForAgePercentile = percentileCalculator.calculatePercentile(
                    type = "head_circumference_for_age",
                    sex = newbornInfo.value.firstOrNull()?.sex ?: "male",
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.age.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.headCircumference.toInt()
                )
            )
        )
    }

    fun isPercentileInNormalLimits(percentileValue: Double): Boolean {
        return percentileCalculator.isPercentileInNormalLimits(percentileValue)
    }

    fun getPercentileResultResource(percentileValue: Double): Int {
        if (isPercentileInNormalLimits(percentileValue)) {
            return R.string.normalPercentileValue
        }
        return R.string.abnormalPercentilaValue
    }

    fun getLengthForAgePercentileInterpretation(percentileValue: Double): String {
        return "${String.format("%.2f", percentileValue)}% of children of the same age are shorter or of the same height as your child, while ${String.format("%.2f", 100 - percentileValue)}% of children of the same age are taller than your child."
    }

    fun getWeightForAgePercentileInterpretation(percentileValue: Double): String {
        return "${String.format("%.2f", percentileValue)}% of children of the same age have a smaller or equal body weight as your child, while ${String.format("%.2f", 100 - percentileValue)}% of children of the same age have a greater body weight than your child."
    }

    fun getWeightForLengthPercentileInterpretation(percentileValue: Double): String {
        return "${String.format("%.2f", percentileValue)}% of children of the same height have a smaller or equal body weight as your child, while ${String.format("%.2f", 100 - percentileValue)}% of children of the same height have a greater body weight than your child."
    }

    fun getHeadCircumferenceForAgePercentileInterpretation(percentileValue: Double): String {
        return "${String.format("%.2f", percentileValue)}% of children of the same age have a smaller or equal head circumference as your child, while ${String.format("%.2f", 100 - percentileValue)}% of children of the same age have a greater head circumference than your child."
    }

    fun getPercentileInterpretation(type: Int, percentileValue: Double): String {
        val applicationContext = ApplicationContext()
        when (resourceHelper.getStringFromResource(id = type)) {
            resourceHelper.getStringFromResource(id = R.string.lengthForAgePercentile) -> return getLengthForAgePercentileInterpretation(
                percentileValue
            )

            resourceHelper.getStringFromResource(id = R.string.weightForAgePercentile) -> return getWeightForAgePercentileInterpretation(
                percentileValue
            )

            resourceHelper.getStringFromResource(id = R.string.weightForLengthPercentile) -> return getWeightForLengthPercentileInterpretation(
                percentileValue
            )

            resourceHelper.getStringFromResource(id = R.string.headCircumferenceForAgePercentile) -> return getHeadCircumferenceForAgePercentileInterpretation(
                percentileValue
            )

            else -> return ""
        }
    }

    fun areAllFieldsChecked(): Boolean {
        return isHeightInLimits(uiState.value.growthAndDevelopmentInfo.length)
                && isWeightInLimits(uiState.value.growthAndDevelopmentInfo.weight)
                && isAgeInLimits(uiState.value.growthAndDevelopmentInfo.age)
                && isHeadCircumferenceInLimits(uiState.value.growthAndDevelopmentInfo.headCircumference)

    }


    fun isHeightInLimits(height: String): Boolean {
        val bottomLimit = 45
        val upperLimit = 110
        if (height.isBlank()) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        } else if (height.toIntOrNull() == null) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.notIntegerError)
        } else if (height.toInt() in 45..110) {
            return true
        } else {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.heightNotInLimits)
            return false
        }
        return false
    }

    fun isWeightInLimits(weight: String): Boolean {
        val limit = 0
        if (weight.isBlank()) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        } else if (weight.toIntOrNull() == null) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.notIntegerError)
        } else if (weight.toInt() > limit) {
            return true
        } else {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.weightNotInLimits)
            return false
        }
        return false
    }

    fun isAgeInLimits(age: String): Boolean {
        val bottomLimit = 0
        val upperLimit = 24
        if (age.isBlank()) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        } else if (age.toIntOrNull() == null) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.notIntegerError)
        } else if (age.toInt() in bottomLimit..upperLimit) {
            return true
        } else {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.ageNotInLimits)
            return false
        }
        return false
    }

    fun isHeadCircumferenceInLimits(headCircumference: String): Boolean {
        val limit = 0
        if (headCircumference.isBlank()) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.emptyInput)
            return false
        } else if (headCircumference.toIntOrNull() == null) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.notIntegerError)
        } else if (headCircumference.toInt() > limit) {
            return true
        } else {
            uiState.value =
                uiState.value.copy(errorMessageResource = R.string.headCircumferenceNotInLimits)
            return false
        }
        return false
    }
}
