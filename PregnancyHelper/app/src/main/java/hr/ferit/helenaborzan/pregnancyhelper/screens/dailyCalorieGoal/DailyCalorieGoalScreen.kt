package hr.ferit.helenaborzan.pregnancyhelper.screens.dailyCalorieGoal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.LabeledTextField
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.ActivityLevel
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.RadioButtonWithLabel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyCalorieGoalScreen(
    viewModel: DailyCalorieGoalViewModel = hiltViewModel(),
    navController : NavController
) {
    val uiState by viewModel.uiState
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightestPink)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        InputSection(
            viewModel = viewModel,
            uiState = uiState,
            modifier = Modifier.weight(0.7f))
        Spacer(modifier = Modifier.weight(0.15f))
        BasicButton(
            modifier = Modifier
                .weight(0.05f)
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
                .height(36.dp),
            text = stringResource(id = R.string.calculateDailyCalorieGoal),
            onClick = {
                viewModel.onCalculateCalorieGoalClick()
            navController.navigate(Screen.PregnancyHomeScreen.route)}
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }
    ErrorDialog(uiState = uiState, viewModel = viewModel)
}

@Composable
fun InputSection(
    viewModel: DailyCalorieGoalViewModel,
    uiState: DailyCalorieGoalUiState,
    modifier: Modifier = Modifier) {
    Column (modifier = modifier
        .fillMaxWidth()
        .padding(24.dp)
        .background(color = Color.White, shape = RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){
        LabeledTextField(
            labelId = R.string.heightLabel,
            unitId = R.string.cmLabel,
            value = uiState.height,
            onValueChange = viewModel::onHeightChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        LabeledTextField(
            labelId = R.string.weightLabel,
            unitId = R.string.kgLabel,
            value = uiState.weight,
            onValueChange = viewModel::onWeightChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        LabeledTextField(
            labelId = R.string.ageLabel,
            unitId = R.string.yearsLabel,
            value = uiState.age,
            onValueChange = viewModel::onAgeChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        ActivityInput(viewModel = viewModel)
    }
}

@Composable
fun ActivityInput(viewModel: DailyCalorieGoalViewModel) {
    var selectedActivity by remember { mutableStateOf(ActivityLevel.SEDENTARY) }
    Column (modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.chooseActivityLevel),
            style = TextStyle(color = DarkGray, fontSize = 16.sp)
        )
        Column {
            RadioButtonWithLabel(
                labelId = R.string.littleOrNoActivity,
                isSelected = selectedActivity == ActivityLevel.SEDENTARY,
                onCheckedChange = {
                    selectedActivity = ActivityLevel.SEDENTARY
                    viewModel.onActivityChange(selectedActivity)
                }
            )
            RadioButtonWithLabel(
                labelId = R.string.lowActivity,
                isSelected = selectedActivity == ActivityLevel.LIGHTLY_ACTIVE,
                onCheckedChange = {
                    selectedActivity = ActivityLevel.LIGHTLY_ACTIVE
                    viewModel.onActivityChange(selectedActivity)
                }
            )
            RadioButtonWithLabel(
                labelId = R.string.moderateActivity,
                isSelected = selectedActivity == ActivityLevel.MODERATELY_ACTIVE,
                onCheckedChange = {
                    selectedActivity = ActivityLevel.MODERATELY_ACTIVE
                    viewModel.onActivityChange(selectedActivity)
                }
            )
            RadioButtonWithLabel(
                labelId = R.string.highActivity,
                isSelected = selectedActivity == ActivityLevel.VERY_ACTIVE,
                onCheckedChange = {
                    selectedActivity = ActivityLevel.VERY_ACTIVE
                    viewModel.onActivityChange(selectedActivity)
                }
            )
            RadioButtonWithLabel(
                labelId = R.string.extremeActivity,
                isSelected = selectedActivity == ActivityLevel.SUPER_ACTIVE,
                onCheckedChange = {
                    selectedActivity = ActivityLevel.SUPER_ACTIVE
                    viewModel.onActivityChange(selectedActivity)
                }
            )
        }
    }
}

@Composable
fun ErrorDialog(uiState: DailyCalorieGoalUiState, viewModel: DailyCalorieGoalViewModel) {
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