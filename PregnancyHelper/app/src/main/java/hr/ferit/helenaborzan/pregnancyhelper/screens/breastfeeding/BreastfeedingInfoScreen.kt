package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ScatterPlot
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.anyToLocalDate
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getHoursAndMins
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getString
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.timesToTimePoints
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.breastfeeding.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingInfoScreen(
    viewModel : NewbornHomeViewModel = hiltViewModel(),
    navController : NavController
){
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())

    val uiState by viewModel.breastfeedingInfoUiState

    val breastfeedingInfo = remember(newbornInfo) {
        newbornInfo.flatMap { it.breastfeedingInfo }
    }

    val availableBreastfeedingDates = breastfeedingInfo.mapNotNull { info ->
        anyToLocalDate(info.startTime)
    }.distinct()

    val bottleInfo = remember(newbornInfo) {
        newbornInfo.flatMap { it.bottleInfo }
    }

    val availableBottleDates = bottleInfo.mapNotNull { info ->
        anyToLocalDate(info.time)
    }.distinct()
    
    var showDatePicker by remember { mutableStateOf(false) }
    
    val breastfeedingInfoByDate = viewModel.getBreastfeedingInfoByDate(breastfeedingInfo)
    val bottleInfoByDate = viewModel.getBottleInfoByDate(bottleInfo)

    
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        ChooseDate(
            onClick = { showDatePicker = true },
            viewModel = viewModel,
            uiState = uiState,
            availableDates = if(uiState.feedingType == "Dojenje") availableBreastfeedingDates
        else availableBottleDates)
        DatePickerDialog(
            showDialog = showDatePicker,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.updateSelectedDate(date)
                showDatePicker = false
            },
            availableDates = if (uiState.feedingType == "Dojenje") availableBreastfeedingDates
            else availableBottleDates
        )
        Spacer(modifier = Modifier.height(24.dp))
        ChooseFeedingType(viewModel = viewModel, uiState = uiState)
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.height(40.dp))
        if(uiState.feedingType == "Dojenje") {
            if(breastfeedingInfoByDate.size > 0) {
                ScatterPlot(
                    times = timesToTimePoints(breastfeedingInfoByDate.map { it.startTime }),
                    yValues = viewModel.getFeedingDuration(breastfeedingInfoByDate),
                    xlabel = stringResource(id = R.string.time),
                    ylabel = stringResource(id = R.string.feedingDuration)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            BreastfeedingHistory(
                breastfeedingInfo = breastfeedingInfoByDate,
                modifier = Modifier.weight(0.8f)
            )
        }
        else{
            if(bottleInfoByDate.size > 0) {
                ScatterPlot(
                    times = timesToTimePoints(bottleInfoByDate.map { it.time }),
                    yValues = bottleInfoByDate.map { it.quantity },
                    xlabel = stringResource(id = R.string.time),
                    ylabel = stringResource(id = R.string.milkQuantityMl)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            BottleHistory(
                bottleInfo = bottleInfoByDate,
                modifier = Modifier.weight(0.8f)
            )
        }
        AddFeeding(
            modifier = Modifier.weight(0.1f),
            onClick = { navController.navigate(Screen.BreastfeedingInputScreen.route)}
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChooseFeedingType(viewModel: NewbornHomeViewModel, uiState: BreastfeedingInfoUiState) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingHistory(
    modifier: Modifier = Modifier,
    breastfeedingInfo : List<BreastfeedingInfo>,
) {
    if (breastfeedingInfo.size > 0) {
        LazyColumn(modifier = modifier) {
            items(breastfeedingInfo.size) {
                val startHours = String.format("%02d",getHoursAndMins(breastfeedingInfo[it].startTime)?.get("hours") ?: 0)
                val startMins =
                    String.format("%02d",getHoursAndMins(breastfeedingInfo[it].startTime)?.get("minutes") ?: 0)
                val endHours = String.format("%02d",getHoursAndMins(breastfeedingInfo[it].endTime)?.get("hours") ?: 0)
                val endMins = String.format("%02d",getHoursAndMins(breastfeedingInfo[it].endTime)?.get("minutes") ?: 0)
                BreastfeedingCard(
                    text = "$startHours:$startMins - $endHours:$endMins, ${breastfeedingInfo[it].getMinutesDifference()}min, ${breastfeedingInfo[it].breast}"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    else{
        Text(text = stringResource(id = R.string.noFeedingHistory),
            modifier = modifier)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChooseDate(
    onClick : () -> Unit,
    viewModel: NewbornHomeViewModel,
    uiState: BreastfeedingInfoUiState,
    availableDates: List<LocalDate>
) {
    val selectedDate = uiState.selectedDate
    val previousDate = selectedDate.minusDays(1)
    val nextDate = selectedDate.plusDays(1)
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        if (availableDates.contains(previousDate)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigate_before_24),
                contentDescription = stringResource(id = R.string.previousDate),
                modifier = Modifier.clickable { viewModel.onPreviousDayClick() }
            )
        }
        Text(
            text = if (viewModel.isSelectedDayToday(uiState.selectedDate)) "Danas" else getString(uiState.selectedDate),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable { onClick() }
        )
        if (availableDates.contains(nextDate)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                contentDescription = stringResource(id = R.string.nextDay),
                modifier = Modifier.clickable { viewModel.onNextDayClick() }
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottleHistory(
    modifier: Modifier = Modifier,
    bottleInfo : List<BottleInfo>
) {
    if (bottleInfo.size > 0) {
        LazyColumn(
            modifier = modifier
        )
        {
            items(bottleInfo.size) {
                val hours = String.format("%02d",getHoursAndMins(bottleInfo[it].time)?.get("hours") ?: 0)
                val mins = String.format("%02d",getHoursAndMins(bottleInfo[it].time)?.get("minutes") ?: 0)
                BreastfeedingCard(
                    text = "$hours:$mins, ${bottleInfo[it].quantity}ml"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    else{
        Text(
            text = stringResource(id = R.string.noFeedingHistory),
            modifier = modifier
        )
    }
}
@Composable
fun BreastfeedingCard(text : String, modifier: Modifier = Modifier) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.breastfeeding_24dp_5f6368_fill0_wght400_grad0_opsz24),
            contentDescription = null,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = text,
            style = TextStyle(color = Color.Black),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    availableDates : List<LocalDate>
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val localDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    return localDate in availableDates
                }
            }
        )

        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val localDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(localDate)
                    }
                    onDismiss()
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
fun AddFeeding(
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Row (horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = LightestPink, shape = RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .width(200.dp)
    ){
        Text(
            text = "+",
            style = TextStyle(color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = stringResource(id = R.string.add),
            style = TextStyle(color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp)
        )
    }
}