package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class EPDSQuestionnaireViewModel @Inject constructor(
    questionnaireRepository: QuestionnaireRepository,
    newbornInfoRepository: NewbornInfoRepository,
    @ApplicationContext context: Context
) : BaseQuestionnaireViewModel(questionnaireRepository, newbornInfoRepository, context) {

    override val fieldName: String = "questionnaireResults"
    override fun getResultMessageResource(score: Int?): Int? {
        return when {
            score == null -> null
            score > 12 -> R.string.positiveEPDStest
            else -> R.string.negativeEPDStest
        }
    }
}