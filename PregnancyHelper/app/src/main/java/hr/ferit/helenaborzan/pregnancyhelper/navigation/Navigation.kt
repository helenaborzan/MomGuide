package hr.ferit.helenaborzan.pregnancyhelper.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInfoScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInputScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory.ChooseCategoryScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer.ContractionsTimerScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.dailyCalorieGoal.DailyCalorieGoalScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.getHelpPostpartum.GetHelpPostpartumScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.getHelpPregnancy.GetHelpPregnancyScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentResultsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.home.LoginAndRegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornNameQuestion.NewbornNameQuestionScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.nutritionDetails.NutritionDetailsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.PregnancyHomeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition.NutritionScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.PregnancyStartQuestion
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.AnxietyQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.DepressionQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.EPDSQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.StressQuestionnaireViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireResults.QuestionnaireResultsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics.AnxietyStatisticsViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics.DepressionStatisticsViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics.EPDSStatisticsViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics.QuestionnaireStatisticsScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics.StressStatisticsViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.recipe.RecipeScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.registration.RegistrationScreen



@RequiresApi(34)
@Composable
fun NavigationController() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =
    Screen.HomeScreen.route)
    {
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
        composable(Screen.AnxietyQuestionnnaireScreen.route){
            val viewModel: AnxietyQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                navigate = { navController.navigate(Screen.PregnancyHomeScreen.route)},
                questionnaireName = "anxietyScale")
        }
        composable(Screen.StressQuestionnaireScreen.route){
            val viewModel: StressQuestionnaireViewModel = hiltViewModel()
            QuestionnaireScreen(
                navController = navController,
                viewModel = viewModel,
                navigate = { navController.navigate(Screen.PregnancyHomeScreen.route)},
                questionnaireName = "stressScale")
        }
        composable(Screen.EPDSQuestionnaireStatisticsScreen.route){
            val viewModel : EPDSStatisticsViewModel = hiltViewModel()
            QuestionnaireStatisticsScreen(
                navController = navController,
                viewModel = viewModel,
                questionnaireName = "postPartumDepressionScale"
            )
        }
        composable(Screen.DepressionQuestionnaireStatisticsScreen.route){
            val viewModel : DepressionStatisticsViewModel = hiltViewModel()
            QuestionnaireStatisticsScreen(
                navController = navController,
                viewModel = viewModel,
                questionnaireName = "depressionScale"
            )
        }
        composable(Screen.AnxietyQuestionnaireStatisticsScreen.route){
            val viewModel : AnxietyStatisticsViewModel = hiltViewModel()
            QuestionnaireStatisticsScreen(
                navController = navController,
                viewModel = viewModel,
                questionnaireName = "anxietyScale"
            )
        }
        composable(Screen.StressQuestionnaireStatisticsScreen.route){
            val viewModel : StressStatisticsViewModel = hiltViewModel()
            QuestionnaireStatisticsScreen(
                navController = navController,
                viewModel = viewModel,
                questionnaireName = "stressScale"
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
            RecipeScreen(navController = navController)
        }
        composable(Screen.NewbornNameQuestionScreen.route){
            NewbornNameQuestionScreen(navController = navController)
        }
        composable(Screen.GetHelpPostPartumScreen.route){
            GetHelpPostpartumScreen(navController = navController)
        }
        composable(Screen.GetHelpPregnancyScreen.route){
            GetHelpPregnancyScreen(navController = navController)
        }
        composable(Screen.PregnancyQuestionnaireResultsScreen.route){
            QuestionnaireResultsScreen(navController = navController)
        }

    }
}
