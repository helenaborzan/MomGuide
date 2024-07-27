package hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    @Named("newborn") private val newbornRepository: BaseInfoRepository,
    @Named("pregnancy") private val pregnancyRepository: BaseInfoRepository,
    private val pregnancyInfoRepository: PregnancyInfoRepository
) : ViewModel(){

    private val _pregnancyInfo = MutableStateFlow<List<PregnancyInfo>>(emptyList())
    fun onNewbornsCategoryClick(){
        viewModelScope.launch{
            newbornRepository.ensureInfoDocument()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onPregnancyCategoryClick(){
        viewModelScope.launch {
            pregnancyRepository.ensureInfoDocument()
            getUsersPregnancyInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUsersPregnancyInfo() {
        viewModelScope.launch {
            pregnancyInfoRepository.getUsersPregnancyInfo()
                .catch { exception ->
                    Log.e("ChooseCategoryViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _pregnancyInfo.value = pregnancyInfo
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun doesPregnancyStartTimeExists() : Boolean{
        getUsersPregnancyInfo()
        return _pregnancyInfo.value.any { it.pregnancyStartDate != null }
    }
}