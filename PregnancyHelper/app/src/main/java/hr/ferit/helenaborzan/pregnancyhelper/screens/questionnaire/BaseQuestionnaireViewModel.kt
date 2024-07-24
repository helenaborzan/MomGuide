package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.Question
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseQuestionnaireViewModel(
    private val questionnaireRepository: QuestionnaireRepository,
    private val repository: BaseInfoRepository,
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState = mutableStateOf(QuestionnaireUiState())
        protected set

    private val finalScore = mutableStateOf(0)

    private val _questionnaire = MutableStateFlow<List<Question>>(emptyList())
    val questionnaire: StateFlow<List<Question>> = _questionnaire.asStateFlow()

    private val _selectedAnswers = MutableLiveData<MutableMap<String, Answer?>>()
    val selectedAnswers: LiveData<MutableMap<String, Answer?>> = _selectedAnswers

    private var questionnaireId: String? = null

    fun initializeQuestionnaire(questionnaireName: String) {
        viewModelScope.launch {
            try {
                // Generate a new questionnaire ID
                questionnaireId = UUID.randomUUID().toString()

                // Save the initial questionnaire result to Firestore
                repository.initializeQuestionnaireResult(questionnaireId!!)

                // Fetch the questionnaire questions
                getQuestionnaire(questionnaireName)

                // Fetch initial answers if any
                fetchInitialAnswers()
            } catch (e: Exception) {
                Log.e("BaseQuestionnaireViewModel", "Error initializing questionnaire", e)
            }
        }
    }
     suspend fun fetchInitialAnswers() {
        viewModelScope.launch {
            try {
                val results = repository.fetchQuestionnaireResults()
                val answersMap = mutableMapOf<String, Answer?>()
                results?.forEach { result ->
                    result.results?.forEach { qanda ->
                        answersMap[qanda.questionId] = qanda.selectedAnswer
                    }
                }
                _selectedAnswers.value = answersMap
            } catch (e: Exception) {
                Log.e("BaseQuestionnaireViewModel", "Error fetching questionnaire results", e)
            }
        }
    }

    fun getQuestionnaire(questionnaireName: String) {
        viewModelScope.launch {
            questionnaireRepository.getQuestionnaire(questionnaireName)
                .catch { exception ->
                    Log.e("BaseQuestionnaireViewModel", "Error fetching questionnaire", exception)
                }
                .collect { questions ->
                    _questionnaire.value = questions
                }
        }
    }

    fun updateScore(questionId: String, answer: Answer?) {
        if(answer!=null) {
            val currentAnswers = _selectedAnswers.value ?: mutableMapOf()
            val previousPoints = currentAnswers[questionId]?.points
            finalScore.value -= previousPoints ?: 0
            currentAnswers[questionId] = answer
            finalScore.value += answer?.points ?: 0
            _selectedAnswers.value = currentAnswers
            Log.e("selected answers", "$currentAnswers")

            viewModelScope.launch {
                try {
                    repository.updateSelectedAnswer(questionnaireId = questionnaireId, questionId = questionId, answer = answer)
                } catch (e: Exception) {
                    Log.e("Update Score", "Error updating selected answer", e)
                }
            }
        }
    }

    abstract fun getResultMessageResource(score: Int?): Int?

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitQuestionnaire() {
        if (areAllQuestionsAnswered()) {
            uiState.value = uiState.value.copy(
                score = finalScore.value,
                resultMessageResource = getResultMessageResource(finalScore.value)
            )
            viewModelScope.launch {
                repository.addQuestionnaireResults(
                    questionnaireResultId = questionnaireId ?: UUID.randomUUID().toString(),
                    score = uiState.value.score!!,
                    resultMessage = context.getString(uiState.value.resultMessageResource!!)
                )
            }
        } else {
            uiState.value = uiState.value.copy(
                errorMessageResource = R.string.questionnaireErrorMessage
            )
        }
    }


    fun areAllQuestionsAnswered(): Boolean {
        for (question in questionnaire.value) {
            if (selectedAnswers.value?.get(question.id) == null) {
                return false
            }
        }
        return true
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }

    fun getSelectedAnswer(questionId: String): Answer? {
        return selectedAnswers.value?.get(questionId)
    }


}