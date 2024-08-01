package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject



abstract class BaseQuestionnaireStatisticsViewModel(
    private val questionnaireRepository: QuestionnaireRepository,
    protected val baseInfoRepository: BaseInfoRepository
) : ViewModel() {
    private val _questionnaire = MutableStateFlow<List<Question>>(emptyList())
    val questionnaire: StateFlow<List<Question>> = _questionnaire.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<String, Answer>>(mapOf())
    val userAnswers: StateFlow<Map<String, Answer>> = _userAnswers.asStateFlow()

    private val _answerStatistics = MutableStateFlow<Map<String, Map<String, Double>>>(mapOf())
    val answerStatistics: StateFlow<Map<String, Map<String, Double>>> = _answerStatistics.asStateFlow()

    fun getQuestionnaireWithStatistics(questionnaireName: String) {
        viewModelScope.launch {
            val questionnaireData = fetchQuestionnaireFromFirestore(questionnaireName)
            _questionnaire.value = questionnaireData

            val userAnswersData = fetchUserAnswersFromFirestore()
            _userAnswers.value = userAnswersData

            val statisticsData = calculateStatistics(questionnaireData)
            _answerStatistics.value = statisticsData
        }
    }



    private suspend fun fetchQuestionnaireFromFirestore(questionnaireName: String): List<Question> {
        return try {
            questionnaireRepository.getQuestionnaire(questionnaireName).first()
        } catch (e: Exception) {
            Log.e("BaseQuestionnaireStatisticsViewModel", "Error fetching questionnaire", e)
            emptyList()
        }
    }

    protected abstract suspend fun fetchUserAnswersFromFirestore(): Map<String, Answer>

    private fun calculateStatistics(questionnaire: List<Question>): Map<String, Map<String, Double>> {
        return questionnaire.associate { question ->
            question.id to question.answers.associate { answer ->
                val totalSelections = question.answers.sumOf { it.selectedNumber }
                val percentage = if (totalSelections > 0) {
                    (answer.selectedNumber.toDouble() / totalSelections) * 100
                } else {
                    0.0
                }
                answer.text to percentage
            }
        }
    }
}