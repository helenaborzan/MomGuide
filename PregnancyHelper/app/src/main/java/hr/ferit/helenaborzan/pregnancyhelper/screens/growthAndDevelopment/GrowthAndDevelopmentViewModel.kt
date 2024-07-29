package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.PercentileCalculator
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.ResourceHelper
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
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
    val bottomHeightLimit = 45
    val upperHeightLimit = 110
    val bottomAgeLimit = 0
    val upperAgeLimit = 24
    val weightLimit = 0
    val headCircumferenceLimit = 0
    fun onSexChange(newValue: String) {
        uiState.value = uiState.value.copy(
            growthAndDevelopmentInfo = uiState.value.growthAndDevelopmentInfo.copy(sex = newValue),
            isRadioButtonChecked = true
        )
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
                    sex = uiState.value.growthAndDevelopmentInfo.sex,
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.age.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.length.toInt()
                ),
                weightForAgePercentile = percentileCalculator.calculatePercentile(
                    type = "weight_age",
                    sex = uiState.value.growthAndDevelopmentInfo.sex,
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.age.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.weight.toInt()
                ),
                weightForLengthPercentile = percentileCalculator.calculatePercentile(
                    type = "weight_length",
                    sex = uiState.value.growthAndDevelopmentInfo.sex,
                    searchCriteria = uiState.value.growthAndDevelopmentInfo.length.toInt(),
                    value = uiState.value.growthAndDevelopmentInfo.weight.toInt()
                ),
                headCircumferenceForAgePercentile = percentileCalculator.calculatePercentile(
                    type = "head_circumference_for_age",
                    sex = uiState.value.growthAndDevelopmentInfo.sex,
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
        return "${percentileValue}% djece iste dobi je niže ili jednake visine kao i vaše dijete, dok je ${100 - percentileValue}% djece iste dobi višlje od vašeg djeteta."
    }

    fun getWeightForAgePercentileInterpretation(percentileValue: Double): String {
        return "${percentileValue}% djece iste dobi ima manju ili jednaku tjelesnu težinu kao i Vaše dijete, dok ${100 - percentileValue}% djece iste dobi ima veću tjelesnu težinu od Vašeg djeteta."
    }

    fun getWeightForLengthPercentileInterpretation(percentileValue: Double): String {
        return "${percentileValue}% djece iste visine ima manju ili jednaku tjelesnu težinu kao i Vaše dijete, dok ${100 - percentileValue}% djece iste dobi ima veću tjelesnu težinu od Vašeg djeteta."
    }

    fun getHeadCircumFerenceForAgePercentileInterpretation(percentileValue: Double): String {
        return "${percentileValue}% djece iste dobi ima manji ili jednak opseg glave od Vašeg djeteta, dok ${100 - percentileValue}% djece iste dobi ima veći opseg glave od Vašeg djeteta."
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

            resourceHelper.getStringFromResource(id = R.string.headCircumferenceForAgePercentile) -> return getHeadCircumFerenceForAgePercentileInterpretation(
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
                && isSexChecked(uiState.value.isRadioButtonChecked)
    }

    fun isSexChecked(isChecked: Boolean): Boolean {
        if (!isChecked) {
            uiState.value = uiState.value.copy(errorMessageResource = R.string.sexNotChecked)
        }
        return isChecked
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
