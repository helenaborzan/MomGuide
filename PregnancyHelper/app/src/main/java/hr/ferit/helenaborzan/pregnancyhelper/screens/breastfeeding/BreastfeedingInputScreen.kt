package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.AnswerRadioButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.LabeledTextField
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.NewbornHomeIconBar
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import java.time.format.DateTimeParseException
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingInputScreen(
    navController : NavController,
    viewModel: BreastfeedingViewModel = hiltViewModel()) {

    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    val uiState by viewModel.uiState

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = DirtyWhite)
            .padding(24.dp)
    ){
        NewbornHomeIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            navController = navController
        )
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(0.8f)
                .background(color = Color.White)
                .padding(24.dp)
        ){
            ChooseFeedingType(viewModel = viewModel, uiState = uiState)
            Spacer(modifier = Modifier.height(12.dp))
            if (uiState.feedingType == "Dojenje") {
                BreastfeedingSpecific(viewModel = viewModel,
                    modifier = Modifier.weight(0.8f))
            } else {
                BottleSpecific(
                    viewModel = viewModel,
                    uiState = uiState,
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
        Row (modifier = Modifier.weight(0.1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ){
            AddButton(uiState = uiState, viewModel = viewModel, navController = navController)
        }
    }
    ErrorDialog(uiState = uiState, viewModel = viewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingSpecific(viewModel: BreastfeedingViewModel, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){
        Row(modifier = Modifier.fillMaxWidth()) {
            LocaleWrapper(locale = Locale.ENGLISH) {
                TimePicker(
                    labelId = R.string.startTime,
                    modifier = Modifier.weight(0.7f),
                    onTimeSelected = { newTime ->
                        viewModel.onStartTimeChange(newTime)
                    },
                    viewModel = viewModel
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            LocaleWrapper(locale = Locale.ENGLISH) {
                TimePicker(
                    labelId = R.string.endTime,
                    modifier = Modifier.weight(0.7f),
                    onTimeSelected = { newTime ->
                        viewModel.onEndTimeChange(newTime)
                    },
                    viewModel = viewModel
                )
            }
        }
        ChooseBreast(viewModel = viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottleSpecific(
    viewModel: BreastfeedingViewModel,
    uiState: BreastfeedingInputUiState,
    modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){
        Row(modifier = Modifier.fillMaxWidth()) {
            TimePicker(
                labelId = R.string.time,
                modifier = Modifier.weight(0.7f),
                onTimeSelected = { newTime ->
                    viewModel.onTimeChange(newTime)
                },
                viewModel = viewModel
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        LabeledTextField(
            labelId = R.string.milkQuantity,
            unitId = R.string.mlLabel,
            value = uiState.quantity,
            onValueChange = viewModel::onQuantityChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = modifier
                .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(16.dp))
                .background(color = Color.White)
                .height(54.dp)
                .padding(vertical = 8.dp),
            fontSize = 24.sp,
            padding = PaddingValues(0.dp)
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChooseFeedingType(viewModel: BreastfeedingViewModel, uiState: BreastfeedingInputUiState) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)){
        FeedingType(
            modifier = Modifier.weight(0.5f),
            text = R.string.breastfeedingType,
            fillColor = if(uiState.feedingType == "Dojenje") LightPink else Color.White,
            onClick = { viewModel.onFeedingTypeChange("Dojenje")}
        )
        FeedingType(
            modifier = Modifier.weight(0.5f),
            text = R.string.formulaType,
            fillColor = if(uiState.feedingType == "Bočica") LightPink else Color.White,
            onClick = { viewModel.onFeedingTypeChange("Bočica")}
        )
    }
}

@Composable
fun FeedingType(
    modifier : Modifier = Modifier,
    fillColor : Color = Color.White,
    @StringRes text : Int,
    onClick : () -> Unit
) {
    Box(modifier = modifier
        .fillMaxHeight()
        .clip(RoundedCornerShape(4.dp))
        .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(4.dp))
        .background(fillColor)
        .clickable { onClick() }
    ){
        Text(
            text = stringResource(id = text),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    @StringRes labelId : Int,
    initialTime: LocalTime = LocalTime.now(),
    onTimeSelected: (LocalTime) -> Unit,
    viewModel : BreastfeedingViewModel
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    var showTimePicker by remember { mutableStateOf(false) }
    var timeText by remember { mutableStateOf(initialTime.format(DateTimeFormatter.ofPattern("HH:mm"))) }

    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ){
        Text(
            text = stringResource(id = labelId),
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = timeText,
                onValueChange = { newValue ->
                    timeText = newValue
                    try {
                        val parsedTime = LocalTime.parse(newValue, timeFormatter)
                        selectedTime = parsedTime
                        onTimeSelected(parsedTime)

                    } catch (e: DateTimeParseException) {
                        viewModel.onWrongTimeInput()
                    }
                },
                leadingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Text("🕒", fontSize = 24.sp)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Pink,
                    unfocusedBorderColor = Pink
                )
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    timeText = selectedTime.format(timeFormatter)
                    onTimeSelected(selectedTime)
                    showTimePicker = false
                }) {
                    Text(
                        text = stringResource(id = R.string.ok),
                        style = TextStyle(color = DarkGray)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = TextStyle(color = DarkGray)
                    )
                }
            },
            text = {
                TimeInput(state = timePickerState)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChooseBreast(viewModel: BreastfeedingViewModel) {
    var selectedBreast by remember { mutableStateOf("") }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.breast),
            style = TextStyle(color = Color.Black, fontSize = 18.sp)
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            RadioButtonSection(
                textId = R.string.left,
                isSelected = selectedBreast == "left breast",
                onCheckedChange = {
                    selectedBreast = "left breast"
                    viewModel.onBreastChange(selectedBreast)
                })
            RadioButtonSection(
                textId = R.string.right,
                isSelected = selectedBreast == "right breast",
                onCheckedChange = {
                    selectedBreast = "right breast"
                    viewModel.onBreastChange(selectedBreast)
                })
            RadioButtonSection(
                textId = R.string.both,
                isSelected = selectedBreast == "both",
                onCheckedChange = {
                    selectedBreast = "both"
                    viewModel.onBreastChange(selectedBreast)
                })
        }
    }
}


@Composable
fun RadioButtonSection(
    @StringRes textId: Int,
    isSelected : Boolean,
    onCheckedChange : (Boolean) -> Unit
){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        AnswerRadioButton(
            isSelected = isSelected,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = stringResource(id = textId),
            style = TextStyle(color = Color.Black, fontSize = 16.sp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddButton(
    uiState : BreastfeedingInputUiState,
    viewModel: BreastfeedingViewModel,
    navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        BasicButton(
            text = stringResource(id = R.string.submit),
            onClick = {
                if (uiState.errorMessageResource == null) {
                    viewModel.onSubmitClick()
                    if(uiState.isAddingSuccesful) {
                        navController.navigate(Screen.BreastfeedingInfoScreen.route)
                    }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ErrorDialog(uiState: BreastfeedingInputUiState, viewModel: BreastfeedingViewModel) {
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
