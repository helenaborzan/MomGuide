package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _foodData = MutableStateFlow<NutritionixResponse?>(null)
    val foodData: StateFlow<NutritionixResponse?> get() = _foodData

    fun searchFood(query: String) {
        viewModelScope.launch {
            foodRepository.searchFood(query) { response ->
                _foodData.value = response
            }
        }
    }
}