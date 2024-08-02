package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireUiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject


@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class PregnancyStartViewModel @Inject constructor(
    private val pregnancyInfoRepository: PregnancyInfoRepository
) : ViewModel() {

    var uiState = mutableStateOf(PregnancyStartUiState())
        private set

    fun onPregnancyStartDateChange(startDate : LocalDate){
        val seconds = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond()
        val nanos = 0
        val pregnancyStartTimeTimestamp = Timestamp(seconds, nanos)
        uiState.value = uiState.value.copy(pregnancyStartDate = pregnancyStartTimeTimestamp)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onWrongDateInput(){
        uiState.value = uiState.value.copy(errorMessageResource = R.string.wrongDateInput)
    }

    fun onSubmitClick(){
        val pregnancyStartDate = uiState.value.pregnancyStartDate
        viewModelScope.launch {
            if (pregnancyStartDate != null) {
                pregnancyInfoRepository.addPregnancyStartDate(
                    pregnancyStartDate = pregnancyStartDate
                )
            }
        }
    }
}