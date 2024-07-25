package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _foodData = MutableStateFlow<Result<NutritionixResponse>?>(null)
    val foodData: StateFlow<Result<NutritionixResponse>?> = _foodData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun searchFood(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _foodData.value = try {
                foodRepository.searchFood(query)
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}