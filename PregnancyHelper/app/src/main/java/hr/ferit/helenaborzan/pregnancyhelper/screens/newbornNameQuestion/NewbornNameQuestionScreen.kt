package hr.ferit.helenaborzan.pregnancyhelper.screens.newbornNameQuestion

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentCalculationUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.GrowthAndDevelopmentViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment.RadioButtonWithLabel
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.AddButton
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.PregnancyStartUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion.PregnancyStartViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewbornNameQuestionScreen(
    viewModel : NewbornNameQuestionViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by remember { viewModel.uiState }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (modifier = Modifier.weight(0.5f),
            verticalArrangement = Arrangement.Bottom){
            NameInput(uiState = uiState, viewModel = viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            SexInput(viewModel = viewModel)
        }


        Column(modifier = Modifier.weight(0.5f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddButton(
                uiState = uiState,
                viewModel = viewModel,
                navController = navController
            )
        }


    }
    ErrorDialog(uiState = uiState, viewModel = viewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(uiState: NewbornNameQuestionUiState, viewModel: NewbornNameQuestionViewModel) {
    Column (modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start) {
        Text(
            text = stringResource(id = R.string.nameInputLabel),
            style = TextStyle(color = DarkGray, fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 1,
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Pink,
                    unfocusedBorderColor = Pink
                )
            )
        }
    }
}

@Composable
fun SexInput(viewModel: NewbornNameQuestionViewModel) {
    var selectedSex by remember { mutableStateOf("") }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start){
        Text(
            text = stringResource(id = R.string.chooseSexLabel),
            style = TextStyle(color = DarkGray, fontSize = 16.sp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButtonWithLabel(
                labelId = R.string.maleLabel,
                isSelected = selectedSex == "male",
                onCheckedChange = {
                    selectedSex = "male"
                    viewModel.onSexChange(selectedSex)
                }
            )
            RadioButtonWithLabel(labelId = R.string.femaleLabel,
                isSelected = selectedSex == "female",
                onCheckedChange = {
                    selectedSex = "female"
                    viewModel.onSexChange(selectedSex)
                }
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddButton(
    uiState : NewbornNameQuestionUiState,
    viewModel: NewbornNameQuestionViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        BasicButton(
            text = stringResource(id = R.string.submit),
            onClick = {
                if (viewModel.areAllFieldsChecked() && uiState.errorMessageResource == null) {
                    viewModel.onSubmitClick()
                    navController.navigate(Screen.NewbornHomeScreen.route)
                }
            },
            borderColor = Pink,
            containerColor = Color.White,
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .size(width = 120.dp, height = 36.dp)
        )
    }
}

@Composable
fun ErrorDialog(uiState: NewbornNameQuestionUiState, viewModel: NewbornNameQuestionViewModel) {
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