package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R

import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.QuestionnaireSection
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.ShowAllQuestionnaireResults
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.ShowQuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightRed
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightYellow
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PregnancyHomeScreen(
    navController: NavController,
    viewModel: PregnancyHomeViewModel = hiltViewModel()
) {
    val pregnancyInfo by viewModel.pregnancyInfo.collectAsState(initial = emptyList())

    val pregnancyStartDate = pregnancyInfo.firstOrNull()?.pregnancyStartDate ?: Timestamp.now()

    val depressionQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.depressionQuestionnaireResults }
    }
    val anxietyQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.anxietyQuestionnaireResults }
    }
    val stressQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.stressQuestionnaireResults }
    }
    val nutritionInfo = remember(pregnancyInfo){
        pregnancyInfo.flatMap { it.nutritionInfo }
    }

    val todaysCalories by viewModel.todaysCalories.collectAsState()

    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()


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
            Spacer(modifier = Modifier.height(40.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                PregnancyProgressCircle(viewModel.getWeeksPregnant(pregnancyStartDate))
            }
            Spacer(modifier = Modifier.height(40.dp))
            NutritionSection(
                navController = navController,
                todaysCalorieIntake = todaysCalories,
                calorieGoal = pregnancyInfo.map { it.dailyCalorieGoal }.firstOrNull(),
                trimester = viewModel.getTrimester(pregnancyStartDate),
                viewModel = viewModel
            )
            RecipeSection(favoriteRecipes = favoriteRecipes, viewModel = viewModel, navController = navController)
            ContractionsTimerSection(navController)
            QuestionnaireSection(
                title = R.string.mentalHealthTitle,
                depressionResults = depressionQuestionnaireResults,
                anxietyResults = anxietyQuestionnaireResults,
                stressResults = stressQuestionnaireResults,
                navController = navController
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersPregnancyInfo()
    }
    LaunchedEffect(nutritionInfo){
        viewModel.calculateTodaysCalorieIntake(nutritionInfo)
        viewModel.getTrimester(pregnancyInfo.map { it.pregnancyStartDate}.firstOrNull() ?: Timestamp.now())
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
fun NutritionSection(
    navController: NavController,
    todaysCalorieIntake : Double,
    calorieGoal : Double?,
    trimester: Int,
    viewModel: PregnancyHomeViewModel
    ) {
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
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        ){
            Row (modifier = Modifier
                .padding(24.dp)
                .height(184.dp)
            ){
                AddFoodButton(modifier = Modifier.weight(0.3f), navController = navController)
                Spacer(modifier = Modifier.weight(0.2f))
                DailyCaloriesInfo(
                    modifier = Modifier.weight(0.5f),
                    todaysCalorieIntake = todaysCalorieIntake,
                    calorieGoal = calorieGoal,
                    trimester = trimester,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            CaloriesRecomendation(trimester = trimester, viewModel = viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AddFoodButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column (
        modifier = modifier
            .background(color = LightestPink, shape = RoundedCornerShape(8.dp))
            .fillMaxHeight()
            .clickable { navController.navigate(Screen.NutritionScreen.route) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "+",
            style = TextStyle(color = DarkGray, fontSize = 36.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = stringResource(id = R.string.add),
            style = TextStyle(color = DarkGray, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun DailyCaloriesInfo(
    modifier: Modifier = Modifier,
    todaysCalorieIntake : Double,
    calorieGoal : Double?,
    trimester : Int,
    navController: NavController,
    viewModel: PregnancyHomeViewModel
){
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CaloriesWithLabel(
            labelId = R.string.dailyCalorieIntake,
            value = todaysCalorieIntake,
            valueColor = Blue,
            navController = navController,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(8.dp))
        CaloriesWithLabel(
            labelId = R.string.goal,
            value = calorieGoal,
            trimester = trimester,
            valueColor = Pink,
            navController = navController,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.seeDetails),
            style = TextStyle(color = Color.LightGray, textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable { navController.navigate(Screen.NutritionDetailsScreen.route) }
        )
    }
}

@Composable
fun CaloriesWithLabel(
    @StringRes labelId : Int,
    trimester: Int,
    value : Double?,
    valueColor : Color,
    navController: NavController,
    viewModel: PregnancyHomeViewModel
) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = stringResource(id = labelId)
        )
        if (value!=null) {
            Text(
                text = if (trimester == 1) "${String.format("%.1f", value)}"
                    else if(trimester == 2) "${String.format("%.1f", value+350)}"
                    else "${String.format("%.1f", value+450)}",
                    style = TextStyle(
                        color = valueColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.DailyCalorieGoalScreen.route)
                    }
                        )
        }
        else{
            Text(
                text = stringResource(id = R.string.calculateDailyCalorieGoal),
                style = TextStyle(
                    color = Color.LightGray,
                    fontSize = 20.sp,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate(Screen.DailyCalorieGoalScreen.route)
                    }
            )
        }
    }
}
@Composable
fun CaloriesWithLabel(
    @StringRes labelId : Int,
    value : Double?,
    valueColor : Color,
    navController: NavController,
    viewModel: PregnancyHomeViewModel
) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = stringResource(id = labelId)
        )
        if (value!=null) {
            Text(
                text = "${String.format("%.1f", value)}",
                style = TextStyle(
                    color = valueColor,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.DailyCalorieGoalScreen.route)
                }
            )
        }
        else{
            Text(
                text = stringResource(id = R.string.calculateDailyCalorieGoal),
                style = TextStyle(
                    color = Color.LightGray,
                    fontSize = 20.sp,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate(Screen.DailyCalorieGoalScreen.route)
                    }
            )
        }
    }
}

@Composable
fun CaloriesRecomendation(
    trimester : Int,
    viewModel: PregnancyHomeViewModel) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){

        Text(
            text = "$trimester. tromjesečje",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(id = viewModel.getTrimesterCalorieRecomendation(trimester) )
        )
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
        Spacer(modifier = Modifier.height(12.dp))
        GoToContractionsTimer(navController)
    }
}

@Composable
fun RecipeSection(
    favoriteRecipes : List<RecipeInfo>,
    viewModel: PregnancyHomeViewModel,
    navController: NavController
) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)){

        Text(
            text = stringResource(id = R.string.recipes),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (favoriteRecipes.size > 0) {
            FavoriteRecipes(
                favoriteRecipes = favoriteRecipes,
                viewModel = viewModel,
                navController = navController
            )
        }
        else{
            NoFavoriteRecipes(navController = navController)
        }
    }
}


@Composable
fun NoFavoriteRecipes(navController: NavController) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(id = R.string.noFavoriteRecipes),
            modifier = Modifier.padding(12.dp)
            )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .clickable { navController.navigate(Screen.RecipeScreen.route) }
        ){
            Icon(
                imageVector = Icons.Filled.Explore,
                tint = Pink,
                contentDescription = null,
                )
            Text(
                text = stringResource(id = R.string.discover)
            )
        }
    }
}
@Composable
fun FavoriteRecipes(
    favoriteRecipes : List<RecipeInfo>,
    viewModel: PregnancyHomeViewModel,
    navController: NavController
) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
    ){
        LazyRow(modifier = Modifier.padding(12.dp)) {
            items(favoriteRecipes.size) {
                RecipeItem(
                    recipe = favoriteRecipes[it],
                    isFavourite = viewModel.isFavourite(
                        recipe = favoriteRecipes[it], favoriteRecipes = favoriteRecipes
                    ),
                    viewModel = viewModel,
                    modifier = Modifier.width(200.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(12.dp))
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp, end = 12.dp)
            .clickable { navController.navigate(Screen.RecipeScreen.route) },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ){
            Icon(
                imageVector = Icons.Filled.Explore,
                tint = DarkGray,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = stringResource(id = R.string.discover),
                style  = TextStyle(color = DarkGray, textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Composable
fun RecipeItem(
    recipe: RecipeInfo,
    isFavourite : Boolean,
    viewModel: PregnancyHomeViewModel,
    modifier : Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(0.7f),
        shape = RoundedCornerShape(8.dp),
        colors = CardColors(containerColor = LightBlue, contentColor = DarkGray, disabledContainerColor = LightBlue, disabledContentColor = DarkGray)
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = recipe.label,
                    style = TextStyle(color = DarkGray, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 40.dp) // Dodajemo padding s desne strane
                        .fillMaxWidth()
                )
                IconButton(
                    onClick = {
                        Log.d("RecipeItem", "Clicked favorite for ${recipe.label}")
                        viewModel.toggleFavorite(RecipeInfo(label = recipe.label, image = recipe.image, url = recipe.url))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavourite) Color.Red else Color.Gray
                    )
                }
            }
            Image(
                painter = rememberAsyncImagePainter(recipe.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("View Recipe")
            }
        }
    }
}
@Composable
fun GoToContractionsTimer(navController: NavController) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.baseline_timer_24),
            contentDescription = null,
            tint = Blue,
            modifier = Modifier
                .weight(0.2f)
                .size(40.dp)
        )
        Column (modifier = Modifier
            .weight(0.6f)
            .padding(12.dp)){
            Text(
                text = stringResource(id = R.string.contractionTimerStart),
                style = TextStyle(color = DarkGray, fontSize = 18.sp)
            )
            Text(
                text = stringResource(id = R.string.contractionsTimerDetails),
                style = TextStyle(color = Color.LightGray, fontSize = 16.sp)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_navigate_next_24),
            contentDescription = null,
            modifier = Modifier
                .weight(0.2f)
                .padding(12.dp)
                .clickable {
                    navController.navigate(Screen.ContractionsTimerScreen.route)
                }
        )
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
    if (uiState.isSignedOut) {
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
fun PregnancyProgressCircle(
    weeksPregnant: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LightestPink,
    progressColor: Color = Pink,
    strokeWidth: Dp = 24.dp
) {
    val progress = weeksPregnant / 40f * 360

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val diameter = size.minDimension - strokeWidthPx
            val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
            val size = Size(diameter, diameter)

            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = progress,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
            )
        }
        Column ( horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "$weeksPregnant.",
                color = DarkGray,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "tjedan trudnoće",
                color = DarkGray,
                fontSize = 24.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireSection(
    @StringRes title : Int,
    navController: NavController,
    depressionResults : List<QuestionnaireResult>,
    anxietyResults : List<QuestionnaireResult>,
    stressResults : List<QuestionnaireResult>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = title),
            style = TextStyle(color = Color.Black, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        QuestionnaireCard(
            titleId = R.string.depressionTitle,
            navigate = { navController.navigate(Screen.DepressionQuestionnaireScreen.route) },
            backgroundColor = LightBlue,
            questionnaireResults = depressionResults
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuestionnaireCard(
            titleId = R.string.anxietyTitle,
            navigate = { navController.navigate(Screen.AnxietyQuestionnnaireScreen.route) },
            backgroundColor = LightRed,
            questionnaireResults = anxietyResults
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuestionnaireCard(
            titleId = R.string.stressTitle,
            navigate = { navController.navigate(Screen.StressQuestionnaireScreen.route) },
            backgroundColor = LightYellow,
            questionnaireResults = stressResults
        )
        Spacer(modifier = Modifier.height(12.dp))

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireCard(
    @StringRes titleId: Int,
    navigate : () -> Unit,
    backgroundColor: Color,
    questionnaireResults : List<QuestionnaireResult>
) {
    var showAllResults by remember {
        mutableStateOf(false)
    }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(id = titleId),
                style = TextStyle(color = DarkGray, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            FillQuestionnaire(navigate = navigate)
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            Row (
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.weight(0.7f)
            ){
                Column (modifier = Modifier.weight(0.8f)){
                    ShowQuestionnaireResult(
                        date = if (questionnaireResults.isNotEmpty())
                            questionnaireResults[questionnaireResults.size-1].date
                        else
                            null,
                        resultMessage = if (questionnaireResults.isNotEmpty())
                            questionnaireResults[questionnaireResults.size-1].resultMessage
                        else
                            stringResource(id = R.string.emptyQuestionnaireResults)
                    )
                    if (showAllResults){
                        ShowAllQuestionnaireResults(questionnaireResult = questionnaireResults)
                    }
                }

                if (questionnaireResults.size > 1) {
                    Icon(
                        painter = if (!showAllResults)
                            painterResource(id = R.drawable.baseline_more_horiz_24)
                        else
                            painterResource(id = R.drawable.baseline_expand_less_24),
                        contentDescription = stringResource(id = R.string.moreIconDescription),
                        tint = DarkGray,
                        modifier = Modifier.weight(0.2f)
                            .clickable {
                                showAllResults = !showAllResults
                            }
                    )
                }
            }
            Row (modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                    contentDescription = "See statistics",
                    tint = DarkGray
                )
            }
        }
    }
}

@Composable
fun FillQuestionnaire(navigate: () -> Unit) {
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
            .clickable { navigate() }){
        Text(
            text = "Ispunite upitnik"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.baseline_navigate_next_24),
            contentDescription = null
        )
    }
}