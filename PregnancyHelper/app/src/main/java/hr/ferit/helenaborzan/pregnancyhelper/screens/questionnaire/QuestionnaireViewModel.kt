package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.Question
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val questionnaireRepository: QuestionnaireRepository,
    private val newbornInfoRepository: NewbornInfoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState = mutableStateOf(QuestionnaireUiState())
        private set

    private val finalScore = mutableStateOf(0)

    private val _questionnaire = MutableStateFlow<List<Question>>(emptyList())
    val questionnaire: StateFlow<List<Question>> = _questionnaire.asStateFlow()

    private val selectedAnswers = mutableMapOf<String, Int?>()


    fun getQuestionnaire() {
        viewModelScope.launch {
            questionnaireRepository.getQuestionnaire()
                .catch { exception ->
                    Log.e("YourViewModel", "Error fetching questionnaire", exception)
                }
                .collect { questions ->
                    _questionnaire.value = questions
                }
        }
    }

    fun updateScore(questionId: String, points: Int?) {
        val previousPoints = selectedAnswers[questionId]
        finalScore.value -= previousPoints ?: 0
        selectedAnswers[questionId] = points
        finalScore.value += points ?: 0
        Log.i("mytag", "score: ${finalScore}")
    }

    fun getResultMessageResource(score: Int?): Int? {
        return when {
            score == null -> null
            score > 12 -> R.string.positiveEPDStest
            else -> R.string.negativeEPDStest
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitQuestionnaire() {
        if (areAllQuestionAnswered()) {
            uiState.value = uiState.value.copy(
                score = finalScore.value,
                resultMessageResource = getResultMessageResource(finalScore.value)
            )
            viewModelScope.launch {
                newbornInfoRepository.addQuestionnaireResults(
                    score = uiState.value.score!!,
                    resultMessage = context.getString(uiState.value.resultMessageResource!!)
                )
            }
        }
        else{
            uiState.value = uiState.value.copy(
                errorMessageResource = R.string.questionnaireErrorMessage
            )
        }

    }

    fun areAllQuestionAnswered () : Boolean {
        for (question in questionnaire.value) {
            if (selectedAnswers[question.id] == null){
                return false
            }
        }
        return true
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }
}
