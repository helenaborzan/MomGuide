package hr.ferit.helenaborzan.pregnancyhelper.screens.nutritionDetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.anyToLocalDate
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getHoursAndMins
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getString
import hr.ferit.helenaborzan.pregnancyhelper.model.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.FoodInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionInfo
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingCard
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.BreastfeedingInfoUiState
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.ChooseDate
import hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding.DatePickerDialog
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeViewModel
import hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyHome.PregnancyHomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionDetailsScreen(
    viewModel: PregnancyHomeViewModel = hiltViewModel(),
    navController : NavController
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val uiState by viewModel.nutritionDetailsUiState

    val pregnancyInfo by viewModel.pregnancyInfo.collectAsState(initial = emptyList())

    val nutritionInfo = remember (pregnancyInfo){
        pregnancyInfo.flatMap { it.nutritionInfo }
    }
    val availableNutritionDetailsDates = nutritionInfo.mapNotNull { info ->
        anyToLocalDate(info.date)
    }.distinct()

    val nutritionDetailsByDate = viewModel.getTodaysNutritionDetails(nutritionInfo)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            navController = navController
        )
        ChooseNutritionDetailsDate(
            onClick = { showDatePicker = true },
            viewModel = viewModel,
            uiState = uiState
        )
        DatePickerDialog(
            showDialog = showDatePicker,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.updateSelectedDate(date)
                showDatePicker = false
            },
            availableDates = availableNutritionDetailsDates
        )
        NutritionDetailsHistory(
            foodInfo = nutritionDetailsByDate.flatMap { it.foodInfo },
            viewModel = viewModel)
    }
    LaunchedEffect(Unit){
        viewModel.getUsersPregnancyInfo()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChooseNutritionDetailsDate(
    onClick : () -> Unit,
    viewModel: PregnancyHomeViewModel,
    uiState: NutritionDetailsUiState
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.baseline_navigate_before_24),
            contentDescription = stringResource(id = R.string.previousDate),
            modifier = Modifier.clickable { viewModel.onPreviousDayClick() }
        )
        Text(
            text = if (viewModel.isSelectedDayToday(uiState.selectedDate)) "Danas" else getString(uiState.selectedDate),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable { onClick() }
        )
        Icon(
            painter = painterResource(id = R.drawable.baseline_navigate_next_24),
            contentDescription = stringResource(id = R.string.nextDay),
            modifier = Modifier.clickable { viewModel.onNextDayClick() }
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionDetailsHistory(
    modifier: Modifier = Modifier,
    foodInfo : List<FoodInfo>,
    viewModel: PregnancyHomeViewModel
) {
    if (foodInfo.size > 0) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)){
            Text(text = "Kalorije: ${foodInfo.sumOf { it.calories ?: 0.0 }}kcal")
            Text(text = "Proteini: ${foodInfo.sumOf { it.protein ?: 0.0 }}g")
            Text(text = "Ugljikohidrati: ${foodInfo.sumOf { it.carbohydrate ?: 0.0 }}g")
            Text(text = "Šećeri: ${foodInfo.sumOf { it.sugars ?: 0.0 }}g")
            Text(text = "Masti: ${foodInfo.sumOf { it.totalFat ?: 0.0 }}g")
            Text(text = "Vlakna: ${foodInfo.sumOf { it.dietaryFiber ?: 0.0 }}g")
            Text(text = "Natrij: ${foodInfo.sumOf { it.sodium ?: 0.0 }}mg")
        }
    }
    else{
    Column (modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text(
                text = stringResource(id = R.string.noNutritionHistory),
                modifier = modifier
            )
        }
    }
}
