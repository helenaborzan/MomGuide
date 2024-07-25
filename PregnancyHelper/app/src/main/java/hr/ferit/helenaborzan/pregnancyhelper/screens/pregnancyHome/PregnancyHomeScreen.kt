package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen

import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.QuestionnaireSection
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.Recomendations
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.ResultDialog
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.SignOutErrorDialog
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PregnancyHomeScreen(
    navController: NavController,
    viewModel: PregnancyHomeViewModel = hiltViewModel()
) {
    val pregnancyInfo by viewModel.pregnancyInfo.collectAsState(initial = emptyList())

    val questionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.questionnaireResults }
    }

    val uiState by viewModel.uiState
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(color = DirtyWhite)
    ) {
        item {
            IconBar(viewModel = viewModel)
            Recomendations()
            NutritionSection(navController = navController)
            ContractionsTimerSection(navController)
            QuestionnaireSection(
                navigate = {navController.navigate(Screen.DepressionQuestionnaireScreen.route)},
                questionnaireResults = questionnaireResults,
                title = R.string.depressionTitle
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersPregnancyInfo()
    }

    SignOutErrorDialog(viewModel = viewModel, uiState = uiState)
    ResultDialog(uiState = uiState, navController = navController)
}

@Composable
fun IconBar(viewModel: PregnancyHomeViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ){
        Icon(
            painter = painterResource(R.drawable.baseline_notifications_24),
            contentDescription = stringResource(id = R.string.notificationIconDescription),
            tint = Color.DarkGray
        )
        Text(
            text = stringResource(id = R.string.signOut),
            style = TextStyle(fontSize = 14.sp, color = DarkGray),
            modifier = Modifier.clickable { viewModel.onSignOutClick() }
        )
    }
}

@Composable
fun NutritionSection(navController: NavController) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.nutritionTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(width = 1.dp, color = DarkGray, shape = RoundedCornerShape(8.dp))
                .clickable {
                    navController.navigate(Screen.NutritionScreen.route)
                }
        ){}
    }
}

@Composable
fun ContractionsTimerSection(navController: NavController) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.contractionsTimerTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(width = 1.dp, color = DarkGray, shape = RoundedCornerShape(8.dp))
                    .clickable{ navController.navigate(Screen.ContractionsTimerScreen.route)}
        ){}
    }
}

@Composable
fun SignOutErrorDialog(
    viewModel : PregnancyHomeViewModel,
    uiState : PregnancyHomeUiState
) {
    uiState.errorMessage?.let { messageId ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(id = R.string.error)) },
            text = { Text(stringResource(id = messageId)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Composable
fun ResultDialog(uiState : PregnancyHomeUiState, navController : NavController) {
    if (uiState.isSignedOut == true) {
        AlertDialog(
            onDismissRequest = {
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            title = { Text(stringResource(id = R.string.signOut)) },
            text = { Text(stringResource(id = R.string.succesfulSignOut)) },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}