package hr.ferit.helenaborzan.pregnancyhelper.screens.dailyCalorieGoal

import hr.ferit.helenaborzan.pregnancyhelper.model.ActivityLevel

data class DailyCalorieGoalUiState(
    val height : String = "",
    val weight : String = "",
    val age : String = "",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val errorMessageResource : Int? = null
)
