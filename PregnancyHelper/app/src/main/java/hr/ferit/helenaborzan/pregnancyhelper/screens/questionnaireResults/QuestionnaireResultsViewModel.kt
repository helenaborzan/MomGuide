package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireResults

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.ResourceHelper
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.pregnancy.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestionnaireResultsViewModel @Inject constructor(
    private val pregnancyInfoRepository: PregnancyInfoRepository,
    private val newbornInfoRepository: NewbornInfoRepository,
    private val resourceHelper: ResourceHelper
) : ViewModel(){


    private val _pregnancyInfo = MutableStateFlow<List<PregnancyInfo>>(emptyList())
    val pregnancyInfo: StateFlow<List<PregnancyInfo>> = _pregnancyInfo.asStateFlow()

    private val _selectedChartType = MutableStateFlow("Depression")
    val selectedChartType: StateFlow<String> = _selectedChartType

    fun selectChartType(chartType: String) {
        _selectedChartType.value = chartType
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsersPregnancyInfo() {
        viewModelScope.launch {
            pregnancyInfoRepository.getUserInfo()
                .catch { exception ->
                    Log.e("PregnancyHomeViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _pregnancyInfo.value = pregnancyInfo
                }
        }
    }

    fun getDepressionResultsLabels(depressionResults : List<QuestionnaireResult>) : List<String>{
        val resultLabels = mutableListOf<String>()
        for(result in depressionResults){
            when(result.resultMessage){
                resourceHelper.getStringFromResource(R.string.positiveDepressionTest) -> resultLabels.add("Positive")
                resourceHelper.getStringFromResource(R.string.borderlineDepressionTest) -> resultLabels.add("Borderline")
                resourceHelper.getStringFromResource(R.string.negativeDepressionTest) -> resultLabels.add("Negative")
            }
        }
        return resultLabels
    }
    fun getAnxietyResultsLabels(anxietyResults : List<QuestionnaireResult>) : List<String>{
        val resultLabels = mutableListOf<String>()
        for(result in anxietyResults){
            when(result.resultMessage){
                resourceHelper.getStringFromResource(R.string.positiveAnxietyTest) -> resultLabels.add("Positive")
                resourceHelper.getStringFromResource(R.string.borderlineAnxietyTest) -> resultLabels.add("Borderline")
                resourceHelper.getStringFromResource(R.string.negativeAnxietyTest) -> resultLabels.add("Negative")
            }
        }
        return resultLabels
    }
    fun getStressResultsLabels(stressResults : List<QuestionnaireResult>) : List<String>{
        val resultLabels = mutableListOf<String>()
        for(result in stressResults){
            when(result.resultMessage){
                resourceHelper.getStringFromResource(R.string.positiveStressTest) -> resultLabels.add("High")
                resourceHelper.getStringFromResource(R.string.borderlineStressTest) -> resultLabels.add("Moderate")
                resourceHelper.getStringFromResource(R.string.negativeStressTest) -> resultLabels.add("Low")
            }
        }
        return resultLabels
    }
}