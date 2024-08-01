package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class StressStatisticsViewModel @Inject constructor(
    private val questionnaireRepository: QuestionnaireRepository,
    protected val pregnancyInfoRepository: PregnancyInfoRepository
) : BaseQuestionnaireStatisticsViewModel(questionnaireRepository, pregnancyInfoRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchUserAnswersFromFirestore(): Map<String, Answer> {
        return try {
            val pregnancyInfoList = pregnancyInfoRepository.getUserInfo().firstOrNull()

            if (pregnancyInfoList == null || pregnancyInfoList.isEmpty()) {
                Log.w("AnxietyStatisticsViewModel", "No pregnancy info found")
                return emptyMap()
            }

            val pregnancyInfo = pregnancyInfoList.first()
            val anxietyQuestionnaireResults = pregnancyInfo.stressQuestionnaireResults

            // Get the most recent questionnaire result
            anxietyQuestionnaireResults.lastOrNull()?.let { lastResult ->
                lastResult.results?.associate { qAndA ->
                    qAndA.questionId to (qAndA.selectedAnswer ?: Answer())
                } ?: emptyMap()
            } ?: emptyMap()
        } catch (e: Exception) {
            Log.e("AnxietyStatisticsViewModel", "Error fetching user answers", e)
            emptyMap()
        }
    }

}