package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.AnswerRadioButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireScreen(
    navController: NavController,
    navigate: () -> Unit,
    viewModel: BaseQuestionnaireViewModel,
    questionnaireName : String
) {
    val questionnaire by viewModel.questionnaire.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState
    Column (modifier = Modifier.background(color = DirtyWhite)){
        GoBackIconBar(modifier = Modifier
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
                QandACard(question = question, viewModel = viewModel)
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    thickness = 1.dp,
                    color = LightGray

                )
            }
        }
        SubmitQuestionnaireButton(
            viewModel = viewModel,
            modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f)
            .padding(horizontal = 24.dp),
            questionnaireName = questionnaireName
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getQuestionnaire(questionnaireName)
    }
    ResultDialog(uiState = uiState, navigate = navigate)
    ErrorDialog(uiState = uiState, viewModel = viewModel)


}

@Composable
fun QandACard(
    question : Question,
    viewModel : BaseQuestionnaireViewModel
){
    var selectedAnswer by remember { mutableStateOf<Answer?>(null) }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(12.dp)
    ){
        Text(
            text = question.questionText,
            style = TextStyle(color = Color.Black, fontSize = 18.sp)
        )
        Column (
            modifier = Modifier.padding(8.dp)
        ){
            for (answer in question.answers){
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AnswerRadioButton(
                        isSelected = selectedAnswer == answer,
                        onCheckedChange = {
                            selectedAnswer = if (it) answer else null
                            viewModel.updateScore(questionId = question.id, selectedAnswer = selectedAnswer)
                        }
                    )
                    Text(
                        text = answer.text,
                        style = TextStyle(color = Color.Black, fontSize = 14.sp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmitQuestionnaireButton(
    modifier: Modifier = Modifier,
    viewModel : BaseQuestionnaireViewModel,
    questionnaireName: String) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        BasicButton(
            text = "Podnesi",
            onClick = { viewModel.onSubmitQuestionnaire(questionnaireName = questionnaireName) },
            borderColor = Pink,
            containerColor = Color.White,
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .size(width = 120.dp, height = 36.dp)
        )
    }
}

@Composable
fun ResultDialog(uiState : QuestionnaireUiState, navigate: () -> Unit) {
    uiState.resultMessageResource?.let { it ->
        AlertDialog(
            onDismissRequest = { navigate() },
            title = { Text(stringResource(id =  R.string.result)) },
            text = { Text(stringResource(id = it)) },
            confirmButton = {
                Button(
                    onClick = { navigate() },
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Composable
fun ErrorDialog(uiState: QuestionnaireUiState, viewModel: BaseQuestionnaireViewModel) {
    uiState.errorMessageResource?.let { it ->
        AlertDialog(
            onDismissRequest = {viewModel.clearError()},
            title = { Text(stringResource(id =  R.string.error)) },
            text = { Text(stringResource(id = it)) },
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




