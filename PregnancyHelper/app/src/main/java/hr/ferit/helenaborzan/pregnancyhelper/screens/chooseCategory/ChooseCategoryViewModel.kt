package hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.newborn.NewbornInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.pregnancy.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    @Named("newborn") private val newbornRepository: BaseInfoRepository,
    @Named("pregnancy") private val pregnancyRepository: BaseInfoRepository
) : ViewModel(){

    private val _pregnancyInfo = MutableStateFlow<List<PregnancyInfo>>(emptyList())
    val pregnancyInfo: StateFlow<List<PregnancyInfo>> = _pregnancyInfo.asStateFlow()

    private val _newbornInfo = MutableStateFlow<List<NewbornInfo>>(emptyList())
    val newbornInfo: StateFlow<List<NewbornInfo>> = _newbornInfo.asStateFlow()
    fun onNewbornsCategoryClick(){
        viewModelScope.launch{
            newbornRepository.ensureInfoDocument()
            getUsersNewbornInfo()
        }
    }

    init{
        getUsersPregnancyInfo()
        getUsersNewbornInfo()
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
            pregnancyRepository.getUserInfo()
                .catch { exception ->
                    Log.e("ChooseCategoryViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _pregnancyInfo.value = pregnancyInfo as List<PregnancyInfo>
                }
        }
    }

    private fun getUsersNewbornInfo() {
        viewModelScope.launch {
            newbornRepository.getUserInfo()
                .catch { exception ->
                    Log.e("ChooseCategoryViewModel", "Error fetching Newborn Info", exception)
                }
                .collect { newbornInfo ->
                    _newbornInfo.value = newbornInfo as List<NewbornInfo>
                }
        }
    }
}