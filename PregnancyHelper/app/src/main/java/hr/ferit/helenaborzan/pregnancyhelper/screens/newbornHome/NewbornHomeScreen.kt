package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireUiState
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewbornHomeScreen(
    navController: NavController,
    viewModel: NewbornHomeViewModel = hiltViewModel()) {
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())
    val questionnaireResults = remember(newbornInfo) {
        newbornInfo.flatMap { it.questionnaireResults }
    }
    val growthAndDevelopmentResults = remember(newbornInfo) {
        newbornInfo.flatMap { it.growthAndDevelopmentResults }
    }
    val uiState by viewModel.uiState
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(color = DirtyWhite)
    ){ item {
        IconBar(viewModel = viewModel)
        Recomendations()
        BreastfeedingSection()
        GrowthAndDevelopmentSection(navController = navController, growthAndDevelopmentResults = growthAndDevelopmentResults)
        PostPartumQuestionnaireSection(
            navController = navController,
            questionnaireResults = questionnaireResults
        )
    }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }

    SignOutErrorDialog(viewModel = viewModel, uiState = uiState)
    ResultDialog(uiState = uiState, navController = navController)
}

@Composable
fun IconBar(viewModel: NewbornHomeViewModel) {
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
fun Recomendations() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(
                color = LightestPink,
                shape = RoundedCornerShape(8.dp)
            )

    ){
        Text(
            text = stringResource(id = R.string.recomendations),
            style = TextStyle(color = DarkGray, fontSize = 16.sp, textDecoration = TextDecoration.Underline),
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 4.dp)
        )
        Text(
            text = "Trenutno nema nikakvih preporuka.",
            style = TextStyle(color = DarkGray, fontSize = 14.sp),
            modifier = Modifier.padding(start = 24.dp, top = 4.dp, end = 24.dp, bottom = 24.dp)
        )
    }
}

@Composable
fun BreastfeedingSection() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.breastfeedingTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(width = 1.dp, color = DarkGray, shape = RoundedCornerShape(8.dp))
        ){}
    }
}

@Composable
fun GrowthAndDevelopmentSection(
    navController: NavController,
    growthAndDevelopmentResults: List<GrowthAndDevelopmentResult>) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ){
        Text(
            text = stringResource(id = R.string.growthAndDevelopmentTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GrowthAndDevelopmentResult(
                modifier = Modifier.weight(2f),
                growthAndDevelopmentResults = growthAndDevelopmentResults,
                navController = navController
            )
            GrowthAndDevelopmentButton(
                text = stringResource(id = R.string.growthAndDevelopmentButtonLabel),
                onClick = { navController.navigate(Screen.GrowthAndDevelopmentCalculationScreen.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GrowthAndDevelopmentResult(
    modifier : Modifier = Modifier,
    growthAndDevelopmentResults : List<GrowthAndDevelopmentResult>,
    navController: NavController
){
    Row (modifier = modifier){
        Text(
            text = if (growthAndDevelopmentResults.isNotEmpty()) stringResource(id = R.string.seeAllGrowthAndDevelopmentResults)
            else stringResource(id = R.string.noGrowthAndDevelopmentResults),
            style = TextStyle(color = DarkGray, fontSize = 14.sp),
            modifier = Modifier
                .padding(4.dp)
        )
        if (growthAndDevelopmentResults.isNotEmpty()) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = null,
                modifier = Modifier.clickable { navController.navigate(Screen.GrowthAndDevelopmentResultsScreen.route) }
            )
        }
    }
}

@Composable
fun GrowthAndDevelopmentButton(
    text : String,
    borderColor : Color = LightestPink,
    containerColor : Color = LightestPink,
    onClick : () -> Unit,
    modifier : Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text (
            text = text,
            style = TextStyle(color = DarkGray, fontSize = 12.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostPartumQuestionnaireSection(
    navController: NavController,
    questionnaireResults : List<QuestionnaireResult>
){
    var showAllResults by remember {
        mutableStateOf(false)
    }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.postPartumDepressionTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Text(
                text = stringResource(id = R.string.fillTheQuestionnaireLabel),
                style = TextStyle(color = Pink, fontSize = 16.sp, textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                    .clickable { navController.navigate(Screen.QuestionnaireScreen.route) }
            )
           showQuestionnaireResult(
               date = if (questionnaireResults.isNotEmpty())
                   questionnaireResults[questionnaireResults.size-1].date
               else
                   null,
               resultMessage = if (questionnaireResults.isNotEmpty())
                   questionnaireResults[questionnaireResults.size-1].resultMessage
               else
                   stringResource(id = R.string.emptyQuestionnaireResults)
           )
            if (questionnaireResults.size > 1) {
                Icon(
                    painter = if (!showAllResults)
                        painterResource(id = R.drawable.baseline_more_horiz_24)
                    else
                        painterResource(id = R.drawable.baseline_expand_less_24),
                    contentDescription = stringResource(id = R.string.moreIconDescription),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            showAllResults = !showAllResults
                        }
                )
            }
            if (showAllResults){
                showAllQuestionnaireResults(questionnaireResult = questionnaireResults)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showQuestionnaireResult(date : Timestamp?, resultMessage : String) {
    if (date != null) {
        val dateYMD = getDate(date)
        Text(
            text = "${dateYMD.getValue("day")}.${dateYMD.getValue("month")}.${dateYMD.getValue("year")}.",
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, bottom = 0.dp, end = 4.dp)
        )
    }
    Text(
        text = resultMessage,
        style = TextStyle(color = Color.Black, fontSize = 16.sp),
        modifier = Modifier.padding(4.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showAllQuestionnaireResults(questionnaireResult: List<QuestionnaireResult>) {
    for (i in questionnaireResult.size - 2 downTo 0) {
        showQuestionnaireResult(
            date = questionnaireResult[i].date,
            resultMessage = questionnaireResult[i].resultMessage)
    }
}

@Composable
fun SignOutErrorDialog(
    viewModel : NewbornHomeViewModel,
    uiState : NewbornHomeUiState
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
fun ResultDialog(uiState : NewbornHomeUiState, navController : NavController) {
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



