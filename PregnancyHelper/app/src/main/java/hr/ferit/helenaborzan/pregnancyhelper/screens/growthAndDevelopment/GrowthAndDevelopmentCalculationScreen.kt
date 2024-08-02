package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment



import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.AnswerRadioButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.LabeledTextField
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue

import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Green
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Red

@Composable
fun GrowthAndDevelopmentCalculationScreen(
    viewModel : GrowthAndDevelopmentViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState
    val showResults by viewModel.showResults
    Column (modifier = Modifier.background(color = LightBlue)) {
        LazyColumn() {
            item {
                GoBackIconBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .weight(0.1f),
                    navController = navController
                )
                InputSection(uiState = uiState, viewModel = viewModel)
                CalculateButton(viewModel = viewModel, uiState = uiState)
                if(showResults){
                    PercentileResultSection(uiState = uiState)
                }
            }
        }
    }
    ErrorDialog(uiState = uiState, viewModel = viewModel)
}

@Composable
fun InputSection(
    uiState : GrowthAndDevelopmentCalculationUiState,
    viewModel: GrowthAndDevelopmentViewModel
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
    ){
        RadioButtonInput(uiState = uiState)
        LabeledTextField(
            labelId = R.string.heightInputLabel,
            unitId = R.string.cmLabel,
            value = uiState.growthAndDevelopmentInfo.length,
            onValueChange = viewModel::onHeightChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        LabeledTextField(
            labelId = R.string.weightInputLabel,
            unitId = R.string.kgLabel,
            value = uiState.growthAndDevelopmentInfo.weight,
            onValueChange = viewModel::onWeightChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        LabeledTextField(
            labelId = R.string.ageInputLabel,
            unitId = R.string.monthsLabel,
            value = uiState.growthAndDevelopmentInfo.age,
            onValueChange = viewModel::onAgeChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
        LabeledTextField(
            labelId = R.string.headCircumferenceLabel,
            unitId = R.string.cmLabel,
            value = uiState.growthAndDevelopmentInfo.headCircumference,
            onValueChange = viewModel::onHeadCircumferenceChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .width(200.dp)
                .height(40.dp)
        )
    }
}

@Composable
fun RadioButtonInput(
    uiState : GrowthAndDevelopmentCalculationUiState,
    viewModel: GrowthAndDevelopmentViewModel = hiltViewModel()
){
        var selectedSex by remember { mutableStateOf("") }
        Column (modifier = Modifier.padding(16.dp)){
        Text(
            text = stringResource(id = R.string.chooseSexLabel),
            style = TextStyle(color = DarkGray, fontSize = 16.sp)
        )
        Row (){
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

@Composable
fun RadioButtonWithLabel(
    @StringRes labelId: Int,
    isSelected : Boolean,
    onCheckedChange: (Boolean) -> Unit
){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        AnswerRadioButton(isSelected = isSelected, onCheckedChange = onCheckedChange)
        Text(
            text = stringResource(id = labelId),
            style = TextStyle(color = Color.Black, fontSize = 14.sp)
        )
    }
}

@Composable
fun CalculationLabel(viewModel: GrowthAndDevelopmentViewModel, uiState: GrowthAndDevelopmentCalculationUiState) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .clickable { if (uiState.errorMessageResource == null) viewModel.onCalculatePercentilesClick() }
    ){
        Icon(
            painter = painterResource(id = R.drawable.baseline_calculate_24),
            contentDescription = null,
            tint = DarkGray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.calculateGrowthPercentiles),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}

@Composable
fun CalculateButton(viewModel: GrowthAndDevelopmentViewModel, uiState: GrowthAndDevelopmentCalculationUiState) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
        .clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically){
        Button(
            onClick = { if (uiState.errorMessageResource == null) viewModel.onCalculatePercentilesClick()  },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(8.dp))
                .height(36.dp),
            enabled = uiState.errorMessageResource == null,
            shape = RoundedCornerShape(8.dp)

        ) {
            Text (
                text = stringResource(id = R.string.calculateGrowthPercentiles),
                style = TextStyle(color = DarkGray, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
            )
        }
    }
}

@Composable
fun PercentileResultSection(uiState: GrowthAndDevelopmentCalculationUiState) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PercentileResult(
            percentileType = R.string.lengthForAgePercentile,
            percentileValue = uiState.growthAndDevelopmentPercentiles.lengthForAgePercentile
        )
        PercentileResult(
            percentileType = R.string.weightForAgePercentile,
            percentileValue = uiState.growthAndDevelopmentPercentiles.weightForAgePercentile
        )
        PercentileResult(
            percentileType = R.string.weightForLengthPercentile,
            percentileValue = uiState.growthAndDevelopmentPercentiles.weightForLengthPercentile
        )
        PercentileResult(
            percentileType = R.string.headCircumferenceForAgePercentile,
            percentileValue = uiState.growthAndDevelopmentPercentiles.headCircumferenceForAgePercentile
        )
    }
}


@Composable
fun PercentileResult(
    @StringRes percentileType : Int,
    percentileValue : Double,
    viewModel : GrowthAndDevelopmentViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.padding(horizontal = 0.dp, vertical = 12.dp)) {
        Row {
            Text(
                text = "${stringResource(id = percentileType)}:  ",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Black
                )
            )
            Text(
                text = "${String.format("%.2f", percentileValue)}",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = if (viewModel.isPercentileInNormalLimits(percentileValue)) Green
                    else Red,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        PercentileInterpretation(percentileType = percentileType, percentileValue = percentileValue)
    }
}

@Composable
fun PercentileInterpretation(
    @StringRes percentileType : Int,
    percentileValue : Double,
    viewModel : GrowthAndDevelopmentViewModel = hiltViewModel()
) {
    var showInterpretation by remember {
        mutableStateOf(false)
    }
    Row {
        Text(
            text = stringResource(id = viewModel.getPercentileResultResource(percentileValue)),
            style = TextStyle(color = Color.Black, fontSize = 14.sp),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = if(!showInterpretation) painterResource(id = R.drawable.baseline_more_horiz_24)
            else painterResource(id = R.drawable.baseline_expand_less_24),
            contentDescription = if(!showInterpretation) stringResource(id = R.string.moreIconDescription)
            else stringResource(id = R.string.lessIconDescription),
            modifier = Modifier
                .weight(0.2f)
                .clickable { showInterpretation = !showInterpretation }
        )
    }
    if (showInterpretation){
        Text(
            text = viewModel.getPercentileInterpretation(type = percentileType, percentileValue = percentileValue),
            style = TextStyle(color = Color.Black, fontSize = 14.sp),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ErrorDialog(uiState: GrowthAndDevelopmentCalculationUiState, viewModel: GrowthAndDevelopmentViewModel) {
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




