package hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.AnswerRadioButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionnaireScreen(
    navController: NavController,
    navigate: () -> Unit,
    viewModel: BaseQuestionnaireViewModel,
    questionnaireName : String,
    questionIndex : Int
) {
    val questionnaire by viewModel.questionnaire.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState

    LaunchedEffect(Unit){
        viewModel.fetchInitialAnswers()
    }

    Column (modifier = Modifier.background(color = DirtyWhite)) {
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        if (questionnaire.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val question = questionnaire.get(questionIndex)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .weight(0.9f),
                verticalArrangement = Arrangement.Top
            ) {
                QandACard(question = question, viewModel = viewModel, modifier = Modifier.weight(0.8f))
                QuestionsNavigation(
                    navController = navController,
                    questionIndex = questionIndex,
                    questionnaireSize = questionnaire.size,
                    questionnaireName = questionnaireName,
                    modifier = Modifier.weight(0.2f)
                )
            }
            if (questionIndex == questionnaire.size - 1) {
                SubmitQuestionnaireButton(
                    viewModel = viewModel, modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.15f)
                        .padding(horizontal = 24.dp)
                )
            }
        }
        LaunchedEffect(Unit) {
            viewModel.getQuestionnaire(questionnaireName)
        }
        ResultDialog(uiState = uiState, navigate = navigate)
        ErrorDialog(uiState = uiState, viewModel = viewModel)
    }
}

@Composable
fun QuestionsNavigation(
    navController: NavController,
    questionIndex: Int,
    questionnaireSize : Int,
    questionnaireName: String,
    modifier : Modifier = Modifier
) {
    Row (modifier = modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row (
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Start
        ){
            if (questionIndex > 0) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_navigate_before_24),
                    contentDescription = stringResource(id = R.string.previousQuestion),
                    tint = Pink,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { navController.popBackStack() }
                )
            }
        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End
        ) {
            if (questionIndex < questionnaireSize - 1) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                    contentDescription = stringResource(id = R.string.nextQuestion),
                    tint = Pink,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            navController.navigate("${getQuestionnaireRoute(questionnaireName)}/${questionIndex + 1}") {
                                restoreState = true
                            }
                        }
                )
            }
        }
    }
}

fun getQuestionnaireRoute(questionnaireName: String): String {
    return when (questionnaireName) {
        "postPartumDepressionScale" -> Screen.EPDSQuestionnaireScreen.route
        "depressionScale" -> Screen.DepressionQuestionnaireScreen.route
        else -> throw IllegalArgumentException("Unknown questionnaire name: $questionnaireName")
    }
}

@Composable
fun QandACard(
    question : Question,
    viewModel : BaseQuestionnaireViewModel,
    modifier: Modifier = Modifier
){
    val selectedAnswer by remember { derivedStateOf { viewModel.getSelectedAnswer(question.id) } }

    // Create a mutable state for the selected answer, to track changes locally
    var localSelectedAnswer by remember { mutableStateOf(selectedAnswer) }

    LaunchedEffect(localSelectedAnswer) {
        viewModel.updateScore(question.id, localSelectedAnswer)
    }

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(12.dp)
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
                        isSelected = localSelectedAnswer == answer,
                        onCheckedChange = {
                            localSelectedAnswer = answer
                            viewModel.updateScore(question.id, answer)
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
fun SubmitQuestionnaireButton(modifier: Modifier = Modifier, viewModel : BaseQuestionnaireViewModel) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        BasicButton(
            text = stringResource(id = R.string.submit),
            onClick = { viewModel.onSubmitQuestionnaire() },
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





