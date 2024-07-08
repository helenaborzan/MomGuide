package hr.ferit.helenaborzan.pregnancyhelper.navigation

sealed class Screen(val route : String){
    object HomeScreen : Screen("HomeScreen")
    object LoginScreen : Screen("LoginScreen")
    object RegistrationScreen : Screen ("RegistrationScreen")
    object ChooseCategoryScreen : Screen ("ChooseCategoryScreen")
    object NewbornHomeScreen : Screen ("NewbornHomeScreen")
    object QuestionnaireScreen : Screen ("QuestionnaireScreen")
    object GrowthAndDevelopmentCalculationScreen : Screen ("GrowthAndDevelopmentCalculationScreen")
    object GrowthAndDevelopmentResultsScreen : Screen("GrowthAndDevelopmentResultsScreen")
}
