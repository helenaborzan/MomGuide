package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.anyToLocalDate
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.ResourceHelper
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.NutritionInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.pregnancy.PregnancyInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.RecipeRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.nutritionDetails.NutritionDetailsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class PregnancyHomeViewModel @Inject constructor(
    private val pregnancyInfoRepository: PregnancyInfoRepository,
    private val accountService: AccountService,
    private val recipeRepository: RecipeRepository,
    private val resourceHelper: ResourceHelper
) : ViewModel(){
    private val _pregnancyInfo = MutableStateFlow<List<PregnancyInfo>>(emptyList())
    val pregnancyInfo: StateFlow<List<PregnancyInfo>> = _pregnancyInfo.asStateFlow()
    var uiState = mutableStateOf(PregnancyHomeUiState())
        private set
    private val _todaysCalories = MutableStateFlow(0.0)
    val todaysCalories: StateFlow<Double> = _todaysCalories.asStateFlow()

    private val _trimester = MutableStateFlow(1)
    val trimester : StateFlow<Int> = _trimester.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    var nutritionDetailsUiState = mutableStateOf(NutritionDetailsUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsersPregnancyInfo() {
        viewModelScope.launch {
            pregnancyInfoRepository.getUserInfo()
                .catch { exception ->
                    Log.e("PregnancyHomeViewModel", "Error fetching Pregnancy Info", exception)
                }
                .collect { pregnancyInfo ->
                    _pregnancyInfo.value = pregnancyInfo
                }
        }
    }
    fun onSignOutClick(){
        viewModelScope.launch {
            try {
                accountService.signOut()
                uiState.value = uiState.value.copy(isSignedOut = true)
            } catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = R.string.signOutError, isSignedOut = false)
            }
        }
    }
    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeksPregnant(pregnancyStartTime : Timestamp) : Int{
        val startInstant: Instant = pregnancyStartTime.toInstant()
        val nowInstant: Instant = Instant.now()
        val daysPregnant = ChronoUnit.DAYS.between(startInstant, nowInstant)
        return (daysPregnant / 7).toInt() + 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrimester(pregnancyStartTime: Timestamp) : Int {
        val weeksPregnant = getWeeksPregnant(pregnancyStartTime)
         return when (weeksPregnant) {
            in 0..12 -> 1
            in 13..26 -> 2
            in 27..45 -> 3
            else -> 3
        }
    }

     suspend fun calculateTodaysCalorieIntake(nutritionInfo: List<NutritionInfo>) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val todaysCalories = nutritionInfo
            .find { it.date.toDate().time == today.time }
            ?.foodInfo
            ?.sumOf { it.calories ?: 0.0 }
            ?: 0.0

        _todaysCalories.value = todaysCalories
    }

    fun getTrimesterCalorieRecomendation(trimester : Int) : Int{
        return when(trimester){
            1 -> R.string.firstTrimesterCalories
            2 -> R.string.secondTrimesterCalories
            else -> R.string.thirdTrimesterCalories
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedDate(newDate : LocalDate){
        nutritionDetailsUiState.value = nutritionDetailsUiState.value.copy(selectedDate = newDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousDayClick() {
        val currentDate = nutritionDetailsUiState.value.selectedDate
        nutritionDetailsUiState.value = nutritionDetailsUiState.value.copy(
            selectedDate = currentDate?.minusDays(1) ?: LocalDate.now().minusDays(1)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextDayClick() {
        val currentDate = nutritionDetailsUiState.value.selectedDate
        nutritionDetailsUiState.value = nutritionDetailsUiState.value.copy(
            selectedDate = currentDate?.plusDays(1) ?: LocalDate.now().plusDays(1)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysNutritionDetails(nutritionDetails: List<NutritionInfo>) : MutableList<NutritionInfo>{
        val date = LocalDate.now()
        val selectedYear = getDate(date).get("year")
        val selectedMonth = getDate(date).get("month")
        val selectedDay = getDate(date).get("day")
        var todaysNutritionDetails = mutableListOf<NutritionInfo>()

        for (row in nutritionDetails){
            if (selectedYear == getDate(row.date).get("year")
                && selectedMonth == getDate(row.date).get("month")
                && selectedDay == getDate(row.date).get("day")
            ){
                todaysNutritionDetails.add(row)
            }
        }
        return todaysNutritionDetails
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNutritionDetailsByDate(nutritionDetails: List<NutritionInfo>): List<NutritionInfo> {
        val selectedDate = nutritionDetailsUiState.value.selectedDate ?: LocalDate.now()
        Log.d("NutritionDetails", "Selected date: $selectedDate")
        Log.d("NutritionDetails", "Total nutrition details: ${nutritionDetails.size}")

        val filteredDetails = nutritionDetails.filter { info ->
            val infoDate = anyToLocalDate(info.date)
            Log.d("NutritionDetails", "Comparing date: $infoDate")
            infoDate == selectedDate
        }

        Log.d("NutritionDetails", "Filtered nutrition details: ${filteredDetails.size}")
        return filteredDetails
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isSelectedDayToday(selectedDate : LocalDate) : Boolean{
        val today = LocalDate.now()
        val selectedYear = getDate(selectedDate).get("year")
        val selectedMonth = getDate(selectedDate).get("month")
        val selectedDay = getDate(selectedDate).get("day")

        return ((selectedYear == getDate(today).get("year")) &&
                (selectedMonth == getDate(today).get("month")) &&
                (selectedDay == getDate(today).get("day")))
    }

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    fun fetchRecipes(query: String) {
        viewModelScope.launch {
            val result = recipeRepository.searchRecipes(query)
            if (result.isSuccess) {
                _recipes.value = result.getOrNull()?.hits?.map { it.recipe } ?: emptyList()
            } else {
                // Handle the failure case
                Log.e("RecipeViewModel", "Failed to fetch recipes: ${result.exceptionOrNull()}")
            }
        }
    }
    val favoriteRecipes = recipeRepository.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleFavorite(recipeInfo: RecipeInfo) {
        viewModelScope.launch {
            Log.d("RecipeViewModel", "Toggling favorite for ${recipeInfo.label}")
            recipeRepository.toggleFavorite(recipeInfo)
        }
    }

    fun isFavourite(recipe : RecipeInfo, favoriteRecipes :  List<RecipeInfo>) : Boolean{
        for (favouriteRecipe in favoriteRecipes){
            if (recipe.url == favouriteRecipe.url)
                return true
        }
        return false
    }

    fun getLikeCount(recipeUrl: String): Flow<Int> {
        return recipeRepository.getLikeCount(recipeUrl)
    }
    fun doesUserHaveDepression(depressionResult: QuestionnaireResult) : Boolean{
        Log.e("pregnancy", "${depressionResult.resultMessage}")
        return depressionResult.resultMessage == resourceHelper.getStringFromResource(R.string.positiveDepressionTest)
                || depressionResult.resultMessage == resourceHelper.getStringFromResource(R.string.borderlineDepressionTest)
    }

    fun doesUserHaveAnxiety(anxietyResult: QuestionnaireResult) : Boolean{
        Log.e("pregnancy", "${anxietyResult.resultMessage}")
        return anxietyResult.resultMessage == resourceHelper.getStringFromResource(R.string.positiveAnxietyTest)
                || anxietyResult.resultMessage == resourceHelper.getStringFromResource(R.string.borderlineAnxietyTest)
    }

    fun doesUserHaveHighStress(stressResult: QuestionnaireResult) : Boolean{
        Log.e("pregnancy", "${stressResult.resultMessage}")
        return stressResult.resultMessage == resourceHelper.getStringFromResource(R.string.positiveStressTest)
    }
}