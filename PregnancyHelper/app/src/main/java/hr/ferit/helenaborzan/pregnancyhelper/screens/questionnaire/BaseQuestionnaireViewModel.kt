package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeQuestionnaire(questionnaireName: String) {
        viewModelScope.launch {
            try {
                // Generate a new questionnaire ID
                questionnaireId = UUID.randomUUID().toString()
                Log.d("InitializeQuestionnaire", "Generated questionnaireId: $questionnaireId")
                // Save the initial questionnaire result to Firestore
                repository.initializeQuestionnaireResult(questionnaireId!!)

                // Fetch the questionnaire questions
                getQuestionnaire(questionnaireName)

            } catch (e: Exception) {
                Log.e("BaseQuestionnaireViewModel", "Error initializing questionnaire", e)
            }
        }
    }
     suspend fun fetchInitialAnswers() {
         viewModelScope.launch {
             try {
                 questionnaireId?.let { id ->
                     val result = repository.fetchQuestionnaireResult(id)
                     val answersMap = mutableMapOf<String, Answer?>()
                     result?.results?.forEach { qanda ->
                         answersMap[qanda.questionId] = qanda.selectedAnswer
                     }
                     _selectedAnswers.value = answersMap
                 } ?: run {
                     Log.e("BaseQuestionnaireViewModel", "QuestionnaireId is null")
                 }
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
        Log.d("UpdateScore", "Function called with questionId: $questionId, answer: $answer")

        if (answer != null) {
            val currentAnswers = _selectedAnswers.value ?: mutableMapOf()
            Log.d("UpdateScore", "Current answers before update: $currentAnswers")

            val previousPoints = currentAnswers[questionId]?.points
            finalScore.value -= previousPoints ?: 0
            currentAnswers[questionId] = answer
            finalScore.value += answer.points ?: 0
            _selectedAnswers.value = currentAnswers

            Log.d("UpdateScore", "Updated answers: $currentAnswers")
            Log.d("UpdateScore", "Updated finalScore: ${finalScore.value}")

            viewModelScope.launch {
                try {
                    Log.d("UpdateScore", "Attempting to update answer in repository")
                    questionnaireId?.let { id ->
                        Log.d("UpdateScore", "QuestionnaireId: $id")
                        repository.updateSelectedAnswer(questionnaireId = id, questionId = questionId, answer = answer)
                        Log.d("UpdateScore", "Answer updated successfully in repository")
                    } ?: run {
                        Log.e("UpdateScore", "Error: questionnaireId is null")
                    }
                } catch (e: Exception) {
                    Log.e("UpdateScore", "Error updating selected answer", e)
                }
            }
        } else {
            Log.e("UpdateScore", "Answer is null")
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