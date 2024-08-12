package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornQuestionnaireResults

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewbornQuestionnaireResultsViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository,
    private val resourceHelper: ResourceHelper
) : ViewModel() {
    private val _newbornInfo = MutableStateFlow<List<NewbornInfo>>(emptyList())
    val newbornInfo: StateFlow<List<NewbornInfo>> = _newbornInfo.asStateFlow()


    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsersNewbornInfo() {
        viewModelScope.launch {
            newbornInfoRepository.getUserInfo()
                .catch { exception ->
                    Log.e("PregnancyHomeViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _newbornInfo.value = pregnancyInfo
                }
        }
    }

    fun getPPDResultsLabels(questionnaireResults : List<QuestionnaireResult>) : List<String> {
        val resultLabels = mutableListOf<String>()
        for (result in questionnaireResults) {
            when (result.resultMessage) {
                resourceHelper.getStringFromResource(R.string.positiveEPDStest) -> resultLabels.add(
                    "Positive"
                )

                resourceHelper.getStringFromResource(R.string.negativeEPDStest) -> resultLabels.add(
                    "Negative"
                )
            }
        }
        return resultLabels
    }

}