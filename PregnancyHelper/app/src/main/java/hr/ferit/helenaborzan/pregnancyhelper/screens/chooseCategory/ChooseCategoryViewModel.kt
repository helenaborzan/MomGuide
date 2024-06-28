package hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    private val newbornInfoRepository: NewbornInfoRepository
) : ViewModel(){

    fun onNewbornsCategoryClick(){
        viewModelScope.launch{
            newbornInfoRepository.ensureNewbornInfoDocument()
        }
    }
}