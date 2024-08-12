package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.PregnantWoman
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ScatterPlot
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.timesToTimePoints
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.AddFeeding
import hr.ferit.helenaborzan.pregnancyhelper.screens.map.MapSection
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Red


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
        IconBar(viewModel = viewModel, navController = navController)
        BabyName(name = newbornInfo.map { it.name }.firstOrNull() ?: "",
            sex = newbornInfo.map { it.sex }.firstOrNull() ?: "",
            navController = navController
        )
        Spacer(modifier = Modifier.height(40.dp))
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
            title = R.string.postPartumDepressionTitle,
            viewModel = viewModel,
            navController = navController
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
fun IconBar(viewModel: NewbornHomeViewModel, navController : NavController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ){
        Box(modifier = Modifier.border(width = 1.dp, color = DarkGray, shape = CircleShape)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_baby_changing_station_24),
                contentDescription = null,
                tint = DarkGray,
                modifier = Modifier.padding(4.dp)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_logout_24),
                contentDescription = null,
                tint = DarkGray,
                modifier = Modifier.clickable { viewModel.onSignOutClick() }
            )
            Text(
                text = stringResource(id = R.string.signOut),
                style = TextStyle(fontSize = 14.sp, color = DarkGray),
                modifier = Modifier.clickable { viewModel.onSignOutClick() }
            )
        }
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
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.breastfeedingAndFormulaTitle),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ChooseType(viewModel = viewModel, uiState = uiState)
        Column (
            modifier = Modifier
                .fillMaxWidth()
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "${todaysBreastfeedingInfo.size} feedings",
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
            modifier = Modifier.height(200.dp)
        )
    }
    else{
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.noFeedingHistoryToday),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.7f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Row (modifier = Modifier.weight(0.2f),
                horizontalArrangement = Arrangement.End){
                Text(
                    text = stringResource(id = R.string.seeAll),
                    style = TextStyle(
                        color = Color.LightGray,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(Screen.BreastfeedingInfoScreen.route)
                        }
                )
            }
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
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "${todaysBottleInfo.size} feedings",
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
                text = stringResource(id = R.string.noFeedingHistoryToday),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.7f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Row (modifier = Modifier.weight(0.2f),
                horizontalArrangement = Arrangement.End){
                Text(
                    text = stringResource(id = R.string.seeAll),
                    style = TextStyle(
                        color = Color.LightGray,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(Screen.BreastfeedingInfoScreen.route)
                        }
                )
            }
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
            .padding(vertical = 12.dp)
    ){
        BasicButton(
            text = stringResource(id = R.string.breastfeedingType),
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
            .padding(12.dp)
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
                modifier = Modifier.weight(0.6f),
                growthAndDevelopmentResults = growthAndDevelopmentResults,
                navController = navController
            )
            Row(modifier = Modifier.weight(0.4f)){
                GrowthAndDevelopmentButton(
                    text = stringResource(id = R.string.growthAndDevelopmentButtonLabel),
                    onClick = { navController.navigate(Screen.GrowthAndDevelopmentCalculationScreen.route) }
                )
        }
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
            style = TextStyle(color = DarkGray, fontSize = 12.sp, fontFamily = FontFamily.SansSerif),
            textAlign = TextAlign.Center
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireSection(
    navigate : () -> Unit,
    questionnaireResults : List<QuestionnaireResult>,
    @StringRes title : Int,
    viewModel: NewbornHomeViewModel,
    navController: NavController
){
    var showAllResults by remember {
        mutableStateOf(false)
    }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(id = title),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (questionnaireResults.isNotEmpty()){
            if (viewModel.doesUserNeedHelp(questionnaireResults.last())){
                GetHelp(
                    navigate = {navController.navigate(Screen.GetHelpPostPartumScreen.route)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp))
            }
        }
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
            Row() {
                Row (modifier = Modifier.weight(0.85f),
                    verticalAlignment = Alignment.Bottom){
                    Column (modifier = Modifier.weight(0.9f)){
                        ShowQuestionnaireResult(
                            date = if (questionnaireResults.isNotEmpty())
                                questionnaireResults[questionnaireResults.size - 1].date
                            else
                                null,
                            resultMessage = if (questionnaireResults.isNotEmpty())
                                questionnaireResults[questionnaireResults.size - 1].resultMessage
                            else
                                stringResource(id = R.string.emptyQuestionnaireResults)
                        )
                    }
                        if (questionnaireResults.size > 1) {
                            Icon(
                                painter = if (!showAllResults)
                                    painterResource(id = R.drawable.baseline_more_horiz_24)
                                else
                                    painterResource(id = R.drawable.baseline_expand_less_24),
                                contentDescription = stringResource(id = R.string.moreIconDescription),
                                modifier = Modifier
                                    .weight(0.1f)
                                    .clickable {
                                        showAllResults = !showAllResults
                                    }
                            )
                        }
                }
                if(questionnaireResults.isNotEmpty()) {
                    Row(
                        modifier = Modifier.weight(0.15f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                            contentDescription = "See statistics",
                            tint = DarkGray,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(Screen.EPDSQuestionnaireStatisticsScreen.route)
                                }
                        )
                    }
                }
            }
            if (showAllResults){
                ShowAllQuestionnaireResults(questionnaireResult = questionnaireResults)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(id = R.string.seeAllResults),
                style = TextStyle(color = DarkGray, fontSize = 14.sp),
                modifier = Modifier.clickable { navController.navigate(Screen.NewbornQuestionnaireResultsScreen.route) }
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = null,
                tint = DarkGray,
                modifier = Modifier.clickable { navController.navigate(Screen.NewbornQuestionnaireResultsScreen.route) }
            )
        }
    }
}

@Composable
fun GetHelp(navigate: () -> Unit, modifier: Modifier) {
    Column (modifier = modifier){
        Row (verticalAlignment = Alignment.CenterVertically){
            Text(
                text = stringResource(id = R.string.getHelp),
                style = TextStyle(color = Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = "Get help",
                modifier = Modifier.clickable {
                    navigate()
            })
        }
        Text(text = stringResource(id = R.string.getHelpDescription), fontSize = 16.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowQuestionnaireResult(date : Timestamp?, resultMessage : String) {
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
fun ShowAllQuestionnaireResults(questionnaireResult: List<QuestionnaireResult>) {
    for (i in questionnaireResult.size - 2 downTo 0) {
        ShowQuestionnaireResult(
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

@Composable
fun BabyName(name : String, sex : String, navController: NavController) {
    val color : Color = if (sex == "female") Pink else Blue

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .size(150.dp)
                .border(width = 1.dp, color = color, shape = CircleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Baby",
                style = TextStyle(
                    color = color,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = name,
                style = TextStyle(
                    color = color,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable{
                    navController.navigate(Screen.NewbornNameQuestionScreen.route)
                }
            )
        }
    }
}



