package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInputUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.LocaleWrapper
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PregnancyStartQuestion(
    viewModel: PregnancyStartViewModel = hiltViewModel(),
    navController : NavController
) {
    val uiState by remember { viewModel.uiState }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocaleWrapper(locale = Locale.ENGLISH) {
            PregnancyStartDatePicker(
                modifier = Modifier.weight(0.5f),
                labelId = R.string.pregnancyStartQuestion,
                onDateSelected = { newDate ->
                    viewModel.onPregnancyStartDateChange(newDate)
                },
                viewModel = viewModel
            )
        }
        Column(modifier = Modifier.weight(0.4f),
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
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun PregnancyStartDatePicker(
    modifier: Modifier = Modifier,
    @StringRes labelId: Int,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    viewModel: PregnancyStartViewModel
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateText by remember { mutableStateOf(initialDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    Column (modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start){
        Text(
            text = stringResource(id = labelId),
            style = TextStyle(color = DarkGray, fontSize = 20.sp),
            modifier = Modifier.padding(vertical = 24.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = dateText,
                onValueChange = { newValue ->
                    dateText = newValue
                    try {
                        val parsedDate = LocalDate.parse(newValue, dateFormatter)
                        selectedDate = parsedDate
                        onDateSelected(parsedDate)
                    } catch (e: DateTimeParseException) {
                        viewModel.onWrongDateInput()
                    }
                },
                leadingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("📅", fontSize = 24.sp)
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        selectedDate = newDate
                        dateText = newDate.format(dateFormatter)
                        onDateSelected(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text(
                        text = stringResource(id = R.string.ok),
                        style = TextStyle(color = DarkGray)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = TextStyle(color = DarkGray)
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(text = "Choose date", modifier = Modifier.padding(12.dp))},
                headline = { Text(text = "Selected date", modifier = Modifier.padding(12.dp))},
                showModeToggle = false
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddButton(
    uiState : PregnancyStartUiState,
    viewModel: PregnancyStartViewModel,
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
                if (uiState.errorMessageResource == null) {
                    viewModel.onSubmitClick()
                    navController.navigate(Screen.PregnancyHomeScreen.route)
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



