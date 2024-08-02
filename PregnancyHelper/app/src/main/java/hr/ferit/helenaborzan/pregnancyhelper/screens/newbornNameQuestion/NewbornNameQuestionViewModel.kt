package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornNameQuestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.PregnancyStartUiState
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewbornNameQuestionViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository
) : ViewModel(){

    var uiState = mutableStateOf(NewbornNameQuestionUiState())
        protected set

    fun onNameChange(newName : String){
        uiState.value = uiState.value.copy(name = newName)
    }

    fun onSexChange(newSex : String){
        uiState.value = uiState.value.copy(sex = newSex)
    }

    fun areAllFieldsChecked() : Boolean{
        return isNameFilled()&&isSexChecked()
    }

    fun isNameFilled() : Boolean{
        if (uiState.value.name.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.blankNameError)
            return false
        }
        return true
    }

    fun isSexChecked() : Boolean{
        if (uiState.value.sex.isBlank()){
            uiState.value = uiState.value.copy(errorMessageResource = R.string.blankSexError)
            return false
        }
        return true
    }
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessageResource = null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmitClick(){
        if(areAllFieldsChecked()) {
            val name = uiState.value.name
            val sex = uiState.value.sex
            viewModelScope.launch {
                if (name != null) {
                    newbornInfoRepository.addBabyName(
                        name = name
                    )
                }
                if (sex != null) {
                    newbornInfoRepository.addBabySex(
                        sex = sex
                    )
                }
            }
        }
    }

}
