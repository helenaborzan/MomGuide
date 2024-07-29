package hr.ferit.helenaborzan.pregnancyhelper.screens.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe
import hr.ferit.helenaborzan.pregnancyhelper.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    fun fetchRecipes(query: String) {
        viewModelScope.launch {
            val result = repository.searchRecipes(query)
            if (result.isSuccess) {
                _recipes.value = result.getOrNull()?.hits?.map { it.recipe } ?: emptyList()
            } else {
                // Handle the failure case
                Log.e("RecipeViewModel", "Failed to fetch recipes: ${result.exceptionOrNull()}")
            }
        }
    }
}