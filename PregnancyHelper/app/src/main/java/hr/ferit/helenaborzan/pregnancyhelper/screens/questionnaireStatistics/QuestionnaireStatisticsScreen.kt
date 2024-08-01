package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaireStatistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.AnswerRadioButton
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import androidx.compose.foundation.shape.RoundedCornerShape




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireStatisticsScreen(
    navController: NavController,
    viewModel: BaseQuestionnaireStatisticsViewModel,
    questionnaireName: String
) {
    val questionnaire by viewModel.questionnaire.collectAsState(initial = emptyList())
    val userAnswers by viewModel.userAnswers.collectAsState(initial = mapOf())
    val answerStatistics by viewModel.answerStatistics.collectAsState(initial = mapOf())


    Column(modifier = Modifier.background(color = DirtyWhite)) {
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .weight(0.9f)
        ) {
            items(questionnaire) { question ->
                QandACard(
                    question = question,
                    userAnswer = userAnswers[question.id],
                    statistics = answerStatistics[question.id] ?: mapOf()
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    thickness = 1.dp,
                    color = LightGray
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getQuestionnaireWithStatistics(questionnaireName)
    }
}

@Composable
fun QandACard(
    question: Question,
    userAnswer: Answer?,
    statistics: Map<String, Double>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = question.questionText,
            style = TextStyle(color = Color.Black, fontSize = 18.sp)
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            for (answer in question.answers) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnswerRadioButton(
                        isSelected = userAnswer?.text == answer.text,
                        onCheckedChange = { /* Do nothing, as this is a statistics view */ }
                    )
                    Text(
                        text = answer.text,
                        style = TextStyle(color = Color.Black, fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.1f%%", statistics[answer.text] ?: 0.0),
                        style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}