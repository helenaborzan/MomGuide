package hr.ferit.helenaborzan.pregnancyhelper.navigation

sealed class Screen(val route : String){
    object HomeScreen : Screen("HomeScreen")
    object LoginScreen : Screen("LoginScreen")
    object RegistrationScreen : Screen ("RegistrationScreen")
    object ChooseCategoryScreen : Screen ("ChooseCategoryScreen")
    object NewbornHomeScreen : Screen ("NewbornHomeScreen")
    object EPDSQuestionnaireScreen : Screen ("EPDSQuestionnaireScreen")
    object GrowthAndDevelopmentCalculationScreen : Screen ("GrowthAndDevelopmentCalculationScreen")
    object GrowthAndDevelopmentResultsScreen : Screen("GrowthAndDevelopmentResultsScreen")
    object PregnancyHomeScreen : Screen("PregnancyHomeScreen")
    object DepressionQuestionnaireScreen : Screen ("DepressionQuestionnaireScreen")
    object ContractionsTimerScreen : Screen ("ContractionsTimerScreen")
    object BreastfeedingInputScreen : Screen ("BreastfeedingInputScreen")
}
