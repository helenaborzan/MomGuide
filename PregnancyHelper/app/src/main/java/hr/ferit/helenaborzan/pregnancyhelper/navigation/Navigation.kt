package hr.ferit.helenaborzan.pregnancyhelper.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInputScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory.ChooseCategoryScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer.ContractionsTimerScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentResultsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.home.LoginAndRegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.ContractionsTimerSection
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.PregnancyHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.DepressionQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.EPDSQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.registration.RegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.PregnancyHelperTheme


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationController() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination =
    Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            LoginAndRegistrationScreen(navController)
        }
        composable(Screen.LoginScreen.route){
            LoginScreen(navController = navController)
        }
        composable(Screen.RegistrationScreen.route){
            RegistrationScreen(navController = navController)
        }
        composable(Screen.ChooseCategoryScreen.route){
            ChooseCategoryScreen(navController = navController)
        }
        composable(Screen.NewbornHomeScreen.route){
            NewbornHomeScreen(navController = navController)
        }
        composable(Screen.EPDSQuestionnaireScreen.route){
            val viewModel: EPDSQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                navigate = { navController.navigate(Screen.NewbornHomeScreen.route)},
                questionnaireName = "postPartumDepressionScale")
        }
        composable(Screen.GrowthAndDevelopmentCalculationScreen.route){
            GrowthAndDevelopmentCalculationScreen(navController = navController)
        }
        composable(Screen.GrowthAndDevelopmentResultsScreen.route){
            GrowthAndDevelopmentResultsScreen(navController = navController)
        }
        composable(Screen.PregnancyHomeScreen.route) {
            PregnancyHomeScreen(navController = navController)
        }
        composable(Screen.DepressionQuestionnaireScreen.route){
            val viewModel: DepressionQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                navigate = { navController.navigate(Screen.PregnancyHomeScreen.route)},
                questionnaireName = "depressionScale")
        }
        composable(Screen.ContractionsTimerScreen.route){
            ContractionsTimerScreen(navController = navController)
        }
        composable(Screen.BreastfeedingInputScreen.route){
            BreastfeedingInputScreen()
        }

    }
}