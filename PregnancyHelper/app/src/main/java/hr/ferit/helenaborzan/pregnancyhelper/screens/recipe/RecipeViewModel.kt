package hr.ferit.helenaborzan.pregnancyhelper.screens.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchRecipes(query)
                if (result.isSuccess) {
                    _recipes.value = result.getOrNull()?.hits?.map { it.recipe } ?: emptyList()
                } else {
                    // Handle the failure case
                    Log.e("RecipeViewModel", "Failed to fetch recipes: ${result.exceptionOrNull()}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    val favoriteRecipes = repository.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleFavorite(recipeInfo: RecipeInfo) {
        viewModelScope.launch {
            Log.d("RecipeViewModel", "Toggling favorite for ${recipeInfo.label}")
            repository.toggleFavorite(recipeInfo)
        }
    }

    fun isFavourite(recipe : Recipe, favoriteRecipes :  List<RecipeInfo>) : Boolean{
        for (favouriteRecipe in favoriteRecipes){
            if (recipe.url == favouriteRecipe.url)
                return true
        }
        return false
    }

    fun getLikeCount(recipeUrl: String): Flow<Int> {
        return repository.getLikeCount(recipeUrl)
    }
}