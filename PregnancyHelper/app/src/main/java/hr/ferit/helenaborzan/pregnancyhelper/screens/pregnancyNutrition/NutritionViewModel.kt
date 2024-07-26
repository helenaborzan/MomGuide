package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.Food
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.repository.FoodRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private val _foodDetails = MutableStateFlow<List<Food?>>(emptyList())
    val foodDetails: StateFlow<List<Food?>> = _foodDetails.asStateFlow()

    fun searchFood(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val searchResult = foodRepository.searchFood(query)
                _foodData.value = searchResult

                if (searchResult.isSuccess) {
                    val commonFoods = searchResult.getOrNull()?.common
                    Log.d("NutritionViewModel", "Common foods: ${commonFoods?.size}")

                    if (commonFoods != null) {
                        val detailedFoods = commonFoods.map { commonFood ->
                            async {
                                try {
                                    foodRepository.getFoodDetails(commonFood.food_name ?: "")
                                        .getOrNull()?.foods?.firstOrNull() // Take the first food item from the response
                                } catch (e: Exception) {
                                    Log.e("NutritionViewModel", "Error fetching details for ${commonFood.food_name}", e)
                                    null
                                }
                            }
                        }.awaitAll()

                        // Flatten the list of lists into a single list of Food?
                        _foodDetails.value = detailedFoods.filterNotNull()
                        Log.d("NutritionViewModel", "Food details size: ${_foodDetails.value.size}")
                    }
                }
            } catch (e: Exception) {
                _foodData.value = Result.failure(e)
                _foodDetails.value = emptyList()
                Log.e("NutritionViewModel", "Error in searchFood", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}