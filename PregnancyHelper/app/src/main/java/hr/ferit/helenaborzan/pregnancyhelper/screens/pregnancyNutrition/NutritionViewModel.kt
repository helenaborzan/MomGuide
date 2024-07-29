package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.Food
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.NutritionixResponse
import hr.ferit.helenaborzan.pregnancyhelper.repository.FoodRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val pregnancyInfoRepository: PregnancyInfoRepository
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
                    val brandedFoods = searchResult.getOrNull()?.branded
                    Log.d("NutritionViewModel", "Common foods: ${commonFoods?.size}, Branded foods: ${brandedFoods?.size}")

                    val allFoods = (commonFoods ?: emptyList()) + (brandedFoods ?: emptyList())
                    Log.d("NutritionViewModel", "All food: ${allFoods.size}")

                    if (allFoods.isNotEmpty()) {
                        val detailedFoods = allFoods.map { food ->
                            async {
                                try {
                                    delay(100)
                                    foodRepository.getFoodDetails(food.food_name ?: "")
                                        .getOrNull()?.foods?.firstOrNull()
                                } catch (e: Exception) {
                                    Log.e("NutritionViewModel", "Error fetching details for ${food.food_name}", e)
                                    null
                                }
                            }
                        }.awaitAll()

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

    fun addUsersFoodIntake(food : Food){
        viewModelScope.launch {
            if (food != null) {
                pregnancyInfoRepository.addUsersFoodIntake(
                    food = food
                )
            }
        }
    }
}