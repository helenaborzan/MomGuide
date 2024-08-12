package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornQuestionnaireResults

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
import androidx.compose.foundation.lazy.LazyColumn
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
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.PPDBarChart
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyQuestionnaireResults.PregnancyQuestionnaireResultsViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewbornQuestionnaireResultsScreen(
    navController: NavController,
    viewModel: NewbornQuestionnaireResultsViewModel = hiltViewModel()
) {
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())

    val questionnaireResults = remember(newbornInfo) {
        newbornInfo.flatMap { it.questionnaireResults }
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
        LazyColumn() { item {
           QuestionnaireResults(questionnaireResults = questionnaireResults, viewModel = viewModel)
        }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireResults(questionnaireResults : List<QuestionnaireResult>, viewModel: NewbornQuestionnaireResultsViewModel) {
    if (questionnaireResults.isEmpty()) {
        Text(
            text = stringResource(id = R.string.noQuestionnaireResults),
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(vertical = 40.dp),
            textAlign = TextAlign.Center
        )
    }
    else {
        val dates = questionnaireResults.mapNotNull { it.date }
        val results = viewModel.getPPDResultsLabels(questionnaireResults)
        val categories = listOf<String>("Negative", "Positive")
        PPDBarChart(dates = dates, results = results, categories = categories)
        Spacer(modifier = Modifier.height(24.dp))
        for (result in questionnaireResults) {
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