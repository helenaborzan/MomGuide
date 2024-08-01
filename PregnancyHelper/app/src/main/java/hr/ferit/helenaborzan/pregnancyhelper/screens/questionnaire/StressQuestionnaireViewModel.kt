package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import javax.inject.Inject

@HiltViewModel
class StressQuestionnaireViewModel @Inject constructor(
    questionnaireRepository: QuestionnaireRepository,
    pregnancyInfoRepository: PregnancyInfoRepository,
    @ApplicationContext context: Context
) : BaseQuestionnaireViewModel(questionnaireRepository, pregnancyInfoRepository, context) {

    override val fieldName = "stressQuestionnaireResults"
    override fun getResultMessageResource(score: Int?): Int? {
        return when {
            score == null -> null
            score in 0..13 -> R.string.negativeStressTest
            score in 14..26 -> R.string.borderlineStressTest
            else -> R.string.positiveStressTest
        }
    }
}