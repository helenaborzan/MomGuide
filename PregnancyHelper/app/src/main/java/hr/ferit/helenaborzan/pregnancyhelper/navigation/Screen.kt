package hr.ferit.helenaborzan.pregnancyhelper.navigation

sealed class Screen(val route : String){
    object HomeScreen : Screen("HomeScreen")
    object LoginScreen : Screen("LoginScreen")
    object RegistrationScreen : Screen ("RegistrationScreen")
    object ChooseCategoryScreen : Screen ("ChooseCategoryScreen")
    object NewbornHomeScreen : Screen ("NewbornHomeScreen")
    object EPDSQuestionnaireScreen : Screen ("EPDSQuestionnaireScreen")
    object AnxietyQuestionnnaireScreen : Screen("AnxietyQuestionnnaireScreen")
    object StressQuestionnaireScreen : Screen("StressQuestionnaireScreen")
    object GrowthAndDevelopmentCalculationScreen : Screen ("GrowthAndDevelopmentCalculationScreen")
    object GrowthAndDevelopmentResultsScreen : Screen("GrowthAndDevelopmentResultsScreen")
    object PregnancyHomeScreen : Screen("PregnancyHomeScreen")
    object DepressionQuestionnaireScreen : Screen ("DepressionQuestionnaireScreen")
    object ContractionsTimerScreen : Screen ("ContractionsTimerScreen")
    object BreastfeedingInputScreen : Screen ("BreastfeedingInputScreen")
    object BreastfeedingInfoScreen : Screen ("BreastfeedingInfoScreen")
    object PregnancyStartQuestionScreen : Screen("PregnancyStartQuestionScreen")
    object NutritionScreen : Screen("NutritionScreen")
    object DailyCalorieGoalScreen : Screen("DailyCalorieGoalScreen")
    object NutritionDetailsScreen : Screen("NutritionDetailsScreen")
    object RecipeScreen : Screen("RecipeScreen")
    object EPDSQuestionnaireStatisticsScreen : Screen("EPDSQuestionnaireStatisticsScreen")
    object DepressionQuestionnaireStatisticsScreen : Screen("DepressionQuestionnaireStatisticsScreen")
    object AnxietyQuestionnaireStatisticsScreen : Screen("AnxietyQuestionnaireStatisticsScreen")
    object StressQuestionnaireStatisticsScreen : Screen("StressQuestionnaireStatisticsScreen")


}
