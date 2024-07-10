package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository

import javax.inject.Inject


@HiltViewModel
class EPDSQuestionnaireViewModel @Inject constructor(
    questionnaireRepository: QuestionnaireRepository,
    newbornInfoRepository: NewbornInfoRepository,
    @ApplicationContext context: Context
) : BaseQuestionnaireViewModel(questionnaireRepository, newbornInfoRepository, context) {

    override fun getResultMessageResource(score: Int?): Int? {
        return when {
            score == null -> null
            score > 12 -> R.string.positiveEPDStest
            else -> R.string.negativeEPDStest
        }
    }
}