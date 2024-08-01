package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import javax.inject.Inject

@HiltViewModel
class DepressionQuestionnaireViewModel @Inject constructor(
    questionnaireRepository: QuestionnaireRepository,
    pregnancyInfoRepository: PregnancyInfoRepository,
    @ApplicationContext context: Context
) : BaseQuestionnaireViewModel(questionnaireRepository, pregnancyInfoRepository, context) {

    override val fieldName = "depressionQuestionnaireResults"
    override fun getResultMessageResource(score: Int?): Int? {
        return when {
            score == null -> null
            score in 0..7 -> R.string.negativeDepressionTest
            score in 8..10 -> R.string.borderlineDepressionTest
            else -> R.string.positiveDepressionTest
        }
    }
}