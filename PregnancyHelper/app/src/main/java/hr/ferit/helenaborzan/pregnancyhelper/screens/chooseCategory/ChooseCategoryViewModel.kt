package hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    @Named("newborn") private val newbornRepository: BaseInfoRepository,
    @Named("pregnancy") private val pregnancyRepository: BaseInfoRepository
) : ViewModel(){

    fun onNewbornsCategoryClick(){
        viewModelScope.launch{
            newbornRepository.ensureInfoDocument()
        }
    }
    fun onPregnancyCategoryClick(){
        viewModelScope.launch {
            pregnancyRepository.ensureInfoDocument()
        }
    }
}