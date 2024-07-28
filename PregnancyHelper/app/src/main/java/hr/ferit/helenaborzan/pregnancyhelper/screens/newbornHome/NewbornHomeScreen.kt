package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ScatterPlot
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.timesToTimePoints
import hr.ferit.helenaborzan.pregnancyhelper.model.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.AddFeeding
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.map.MapSection
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
    val breastfeedingInfo = remember(newbornInfo) {
        newbornInfo.flatMap { it.breastfeedingInfo }
    }
    val bottleInfo = remember (newbornInfo){
        newbornInfo.flatMap { it.bottleInfo }
    }

    val todaysBreastfeedingInfo = viewModel.getTodaysBreastfeedingInfo(breastfeedingInfo = breastfeedingInfo)
    val todaysBottleInfo = viewModel.getTodaysBottleInfo(bottleInfo = bottleInfo)

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
        BreastfeedingSection(
            navController = navController,
            uiState = uiState,
            viewModel = viewModel,
            todaysBreastfeedingInfo = todaysBreastfeedingInfo,
            todaysBottleInfo = todaysBottleInfo
        )
        GrowthAndDevelopmentSection(navController = navController, growthAndDevelopmentResults = growthAndDevelopmentResults)
        QuestionnaireSection(
            navigate = {navController.navigate(Screen.EPDSQuestionnaireScreen.route)},
            questionnaireResults = questionnaireResults,
            title = R.string.postPartumDepressionTitle
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingSection(
    viewModel: NewbornHomeViewModel,
    uiState: NewbornHomeUiState,
    navController: NavController,
    todaysBreastfeedingInfo: List<BreastfeedingInfo>,
    todaysBottleInfo : List<BottleInfo>
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.breastfeedingAndFormulaTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ChooseType(viewModel = viewModel, uiState = uiState)
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (uiState.feedingType == "Dojenje"){
                Breastfeeding(
                    todaysBreastfeedingInfo = todaysBreastfeedingInfo,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            else{
                Bottle(
                    todaysBottleInfo = todaysBottleInfo,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddFeeding(onClick = { navController.navigate(Screen.BreastfeedingInputScreen.route) })
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Breastfeeding(
    todaysBreastfeedingInfo: List<BreastfeedingInfo>,
    viewModel: NewbornHomeViewModel,
    navController: NavController
) {
    if (todaysBreastfeedingInfo.size > 0){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "${todaysBreastfeedingInfo.size} hranjenja",
                style = TextStyle(color = Pink)
            )
            Text(
                text = stringResource(id = R.string.today),
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(id = R.string.seeAll),
                style = TextStyle(color = Color.LightGray, textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.BreastfeedingInfoScreen.route)
                }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        ScatterPlot(
            times = timesToTimePoints(todaysBreastfeedingInfo.map { it.startTime }),
            yValues = viewModel.getFeedingDuration(todaysBreastfeedingInfo),
            xlabel = stringResource(id = R.string.time),
            ylabel = stringResource(id = R.string.feedingDuration),
            modifier = Modifier.height(360.dp)
        )
    }
    else{
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.noFeedingHistoryToday)
            )
            Text(
                text = stringResource(id = R.string.seeAll),
                style = TextStyle(
                    color = Color.LightGray,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.BreastfeedingInfoScreen.route)
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Bottle(
    todaysBottleInfo: List<BottleInfo>,
    viewModel: NewbornHomeViewModel,
    navController: NavController
) {
    if (todaysBottleInfo.size > 0){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "${todaysBottleInfo.size} hranjenja",
                style = TextStyle(color = Pink)
            )
            Text(
                text = stringResource(id = R.string.today),
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(id = R.string.seeAll),
                style = TextStyle(color = Color.LightGray, textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.BreastfeedingInfoScreen.route)
                }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        ScatterPlot(
            times = timesToTimePoints(todaysBottleInfo.map { it.time }),
            yValues = todaysBottleInfo.map { it.quantity },
            xlabel = stringResource(id = R.string.time),
            ylabel = stringResource(id = R.string.milkQuantityMl),
            modifier = Modifier.height(360.dp)
        )
    }
    else{
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.noFeedingHistoryToday)
            )
            Text(
                text = stringResource(id = R.string.seeAll),
                style = TextStyle(
                    color = Color.LightGray,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.BreastfeedingInfoScreen.route)
                }
            )
        }
    }
}



@Composable
fun ChooseType(viewModel: NewbornHomeViewModel, uiState: NewbornHomeUiState) {
    Row (
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ){
        BasicButton(
            text = stringResource(id = R.string.breastfeedingTitle),
            onClick = { viewModel.onFeedingTypeChangeHome("Dojenje") },
            containerColor = if (uiState.feedingType == "Dojenje") LightestPink else Color.White,
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                .size(width = 120.dp, height = 36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicButton(
            text = stringResource(id = R.string.formulaType),
            onClick = { viewModel.onFeedingTypeChangeHome("Bočica") },
            containerColor = if (uiState.feedingType == "Bočica") LightestPink else Color.White,
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                .size(width = 120.dp, height = 36.dp)
        )
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
                onClick = { navController.navigate(Screen.GrowthAndDevelopmentCalculationScreen.route) }
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
    modifier : Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Text (
            text = text,
            style = TextStyle(color = DarkGray, fontSize = 12.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireSection(
    navigate : () -> Unit,
    questionnaireResults : List<QuestionnaireResult>,
    @StringRes title : Int
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
            text = stringResource(id = title),
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
                    .clickable { navigate() }
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
            if (Build.VERSION.SDK_INT >= 34) {
                MapSection()
            }
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



