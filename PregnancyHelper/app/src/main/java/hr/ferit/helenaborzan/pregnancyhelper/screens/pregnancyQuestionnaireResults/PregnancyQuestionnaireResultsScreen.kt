package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyQuestionnaireResults

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BarChart
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightRed
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightYellow
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PregnancyQuestionnaireResultsScreen(
    navController: NavController,
    viewModel: PregnancyQuestionnaireResultsViewModel = hiltViewModel()
) {
    val selectedChartType by viewModel.selectedChartType.collectAsState()
    val pregnancyInfo by viewModel.pregnancyInfo.collectAsState(initial = emptyList())
    val depressionQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.depressionQuestionnaireResults.reversed() }
    }
    val anxietyQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.anxietyQuestionnaireResults.reversed() }
    }
    val stressQuestionnaireResults = remember(pregnancyInfo) {
        pregnancyInfo.flatMap { it.stressQuestionnaireResults.reversed() }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(
            color = DirtyWhite
        )
        .padding(12.dp)
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            navController = navController
        )
        Spacer(modifier = Modifier.height(12.dp))
        ChooseQuestionnaireCategory(viewModel = viewModel)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn() { item {
            if (selectedChartType == "Depression"){
                DepressionResults(depressionResults = depressionQuestionnaireResults, viewModel = viewModel)
            }
            else if(selectedChartType == "Anxiety"){
                AnxietyResults(anxietyResults = anxietyQuestionnaireResults, viewModel = viewModel)
            }
            else{
                StressResults(stressResults = stressQuestionnaireResults, viewModel = viewModel)
            }
        }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersPregnancyInfo()
    }
}

@Composable
fun ChooseQuestionnaireCategory(viewModel: PregnancyQuestionnaireResultsViewModel) {
    val selectedChartType by viewModel.selectedChartType.collectAsState()
    LazyRow (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)) {
        item {
            BasicButton(
                text = stringResource(id = R.string.depressionTitle),
                onClick = { viewModel.selectChartType("Depression") },
                containerColor = if (selectedChartType == "Depression") LightPink else Color.White,
                modifier = Modifier
                    .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                    .size(width = 120.dp, height = 36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicButton(
                text = stringResource(id = R.string.anxietyTitle),
                onClick = { viewModel.selectChartType("Anxiety")},
                containerColor = if (selectedChartType == "Anxiety") LightPink else Color.White,
                modifier = Modifier
                    .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                    .size(width = 120.dp, height = 36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicButton(
                text = stringResource(id = R.string.stressTitle),
                onClick = { viewModel.selectChartType("Stress")},
                containerColor = if (selectedChartType == "Stress") LightPink else Color.White,
                modifier = Modifier
                    .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                    .size(width = 120.dp, height = 36.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DepressionResults(depressionResults : List<QuestionnaireResult>, viewModel: PregnancyQuestionnaireResultsViewModel) {
    if (depressionResults.isEmpty()) {
        Text(
            text = stringResource(id = R.string.noQuestionnaireResults),
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(vertical = 40.dp),
            textAlign = TextAlign.Center
        )
    }
    else {
        val dates = depressionResults.reversed().mapNotNull { it.date }
        val results = viewModel.getDepressionResultsLabels(depressionResults.reversed())
        val categories = listOf<String>("Negative", "Borderline", "Positive")
        BarChart(dates = dates, results = results, categories = categories, barColor = LightBlue)
        Spacer(modifier = Modifier.height(24.dp))
        for (result in depressionResults) {
            ResultCard(result = result)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnxietyResults(anxietyResults : List<QuestionnaireResult>, viewModel: PregnancyQuestionnaireResultsViewModel) {
    if (anxietyResults.isEmpty()) {
        Text(
            text = stringResource(id = R.string.noQuestionnaireResults),
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(vertical = 40.dp),
            textAlign = TextAlign.Center

        )
    }
    else {
        val dates = anxietyResults.reversed().mapNotNull { it.date }
        val results = viewModel.getAnxietyResultsLabels(anxietyResults.reversed())
        val categories = listOf<String>("Negative", "Borderline", "Positive")
        BarChart(dates = dates, results = results, categories = categories, barColor = LightRed)
        Spacer(modifier = Modifier.height(24.dp))
        for (result in anxietyResults) {
            ResultCard(result = result)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StressResults(stressResults : List<QuestionnaireResult>, viewModel: PregnancyQuestionnaireResultsViewModel) {
    if (stressResults.isEmpty()) {
        Text(
            text = stringResource(id = R.string.noQuestionnaireResults),
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(vertical = 40.dp),
            textAlign = TextAlign.Center
        )
    }
    else {
        val dates = stressResults.reversed().mapNotNull { it.date }
        val results = viewModel.getStressResultsLabels(stressResults.reversed())
        val categories = listOf<String>("Low", "Moderate", "High")
        BarChart(dates = dates, results = results, categories = categories, barColor = LightYellow)
        Spacer(modifier = Modifier.height(24.dp))
        for (result in stressResults) {
            ResultCard(result = result)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultCard(result: QuestionnaireResult) {
    val date = result.date ?: Timestamp.now()
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "${getDate(date).get("day")}.${getDate(date).get("month")}.${getDate(date).get("year")}.",
            style = TextStyle(color = DarkGray, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = "${result.resultMessage}",
            style = TextStyle(color = DarkGray),
            modifier = Modifier.padding(12.dp)
        )
    }
}