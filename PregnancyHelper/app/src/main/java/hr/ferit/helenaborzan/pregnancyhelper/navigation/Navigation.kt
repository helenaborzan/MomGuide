package hr.ferit.helenaborzan.pregnancyhelper.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInfoScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInputScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory.ChooseCategoryScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer.ContractionsTimerScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.dailyCalorieGoal.DailyCalorieGoalScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentResultsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.home.LoginAndRegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.nutritionDetails.NutritionDetailsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.ContractionsTimerSection
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.NutritionSection
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.PregnancyHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition.NutritionScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.PregnancyStartQuestion
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.BaseQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.DepressionQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.EPDSQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.recipe.RecipeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.registration.RegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.PregnancyHelperTheme



@RequiresApi(Build.VERSION_CODES.S)
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
        composable(Screen.EPDSQuestionnaireScreen.route) {
            val viewModel: EPDSQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                questionIndex = 0,
                questionnaireName = "postPartumDepressionScale",
                navigate = { navController.navigate(Screen.NewbornHomeScreen.route) }
            )
        }
        composable("${Screen.EPDSQuestionnaireScreen.route}/{index}") { backStackEntry ->
            val viewModel: EPDSQuestionnaireViewModel = hiltViewModel()
            val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                questionIndex = index,
                questionnaireName = "postPartumDepressionScale",
                navigate = { navController.navigate(Screen.NewbornHomeScreen.route) }
            )
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
        composable(Screen.DepressionQuestionnaireScreen.route) {
            val viewModel: DepressionQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                questionIndex = 0,
                questionnaireName = "depressionScale",
                navigate = { navController.navigate(Screen.PregnancyHomeScreen.route) }
            )
        }
        composable("${Screen.DepressionQuestionnaireScreen.route}/{index}") { backStackEntry ->
            val viewModel: DepressionQuestionnaireViewModel = hiltViewModel()
            val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                questionIndex = index,
                questionnaireName = "depressionScale",
                navigate = { navController.navigate(Screen.PregnancyHomeScreen.route) }
            )
        }
        composable(Screen.ContractionsTimerScreen.route){
            ContractionsTimerScreen(navController = navController)
        }
        composable(Screen.BreastfeedingInputScreen.route){
            BreastfeedingInputScreen(navController)
        }
        composable(Screen.BreastfeedingInfoScreen.route){
            BreastfeedingInfoScreen(navController = navController)
        }
        composable(Screen.PregnancyStartQuestionScreen.route){
            PregnancyStartQuestion(navController = navController)
        }
        composable(Screen.NutritionScreen.route){
            NutritionScreen(navController = navController)
        }
        composable(Screen.DailyCalorieGoalScreen.route){
            DailyCalorieGoalScreen(navController = navController)
        }
        composable(Screen.NutritionDetailsScreen.route){
            NutritionDetailsScreen(navController = navController)
        }
        composable(Screen.RecipeScreen.route){
            RecipeScreen()
        }

    }
}
