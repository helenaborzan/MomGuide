package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.repository.headCircumferenceForAgeData
import hr.ferit.helenaborzan.pregnancyhelper.repository.heightForAgeData
import hr.ferit.helenaborzan.pregnancyhelper.repository.weightForAgeData
import hr.ferit.helenaborzan.pregnancyhelper.repository.weightForHeightData
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.GrowthAndDevelopmentButton
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink


@Composable
fun GrowthAndDevelopmentResultsScreen(
    viewModel: NewbornHomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())
    val growthAndDevelopmentResults = remember(newbornInfo) {
        newbornInfo.flatMap { it.growthAndDevelopmentResults.reversed() }
    }
    val showDialog = viewModel.showDialog.value
    val selectedChartType by viewModel.selectedChartType.collectAsState()

    val chartData =  when (selectedChartType) {
        "lengthForAge" -> heightForAgeData
        "weightForAge" -> weightForAgeData
        "weightForLength" -> weightForHeightData
        "headCircumferenceForAge" -> headCircumferenceForAgeData
        else -> emptyList()
    }

    LaunchedEffect(growthAndDevelopmentResults, selectedChartType) {
        viewModel.updatePoints(growthAndDevelopmentResults, selectedChartType)
    }

    val points by viewModel.points.collectAsState()



    Column (modifier = Modifier.background(color = DirtyWhite)){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        ChoosePercentile(viewModel = viewModel)
        GrowthPercentileChart(
            data = chartData,
            points = points,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
            )
        if(growthAndDevelopmentResults.size > 0) {
            LazyColumn(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                items(growthAndDevelopmentResults.withIndex().toList()) { (index, result) ->
                    Log.d("LazyColumn", "Rendering item at index: $index")
                    PercentilesResult(
                        growthAndDevelopmentResult = result,
                        growthAndDevelopmentResultIndex = index,
                        viewModel = viewModel,
                    )
                    Log.d("PercentilesUI", "Index: ${index}, Height: ${result.growthAndDevelopmentInfo.length}")
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        else{
            EmptyResults(
                navController = navController,
                modifier = Modifier.weight(0.9f)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }

}

@Composable
fun EmptyResults(navController: NavController, modifier : Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()

    ){
        Text(
            text = stringResource(id = R.string.noPreviousResults),
            style = TextStyle(fontSize = 20.sp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        GrowthAndDevelopmentButton(
            text = stringResource(id = R.string.growthAndDevelopmentButtonLabel),
            onClick = { navController.navigate(Screen.GrowthAndDevelopmentCalculationScreen.route) },
            modifier = Modifier.height(60.dp)
        )
    }
}
@Composable
fun PercentilesResult(
    growthAndDevelopmentResult: GrowthAndDevelopmentResult,
    growthAndDevelopmentResultIndex : Int,
    viewModel: NewbornHomeViewModel
    ) {
    Log.d("PercentilesResult", "Composable called with index: $growthAndDevelopmentResultIndex")
    val date = growthAndDevelopmentResult.growthAndDevelopmentInfo.date
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(4.dp))
            .background(Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 12.dp, bottom = 0.dp, end = 12.dp)
        ) {
            if (date != null) {
                val dateYMD = getDate(date)
                Text(
                    text = "${dateYMD.getValue("day")}.${dateYMD.getValue("month")}.${
                        dateYMD.getValue(
                            "year"
                        )
                    }.",
                    style = TextStyle(color = Color.Black, fontSize = 16.sp)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                contentDescription = stringResource(id = R.string.deletePercentileResult),
                tint = DarkGray,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        Log.d("PercentilesResult", "Delete clicked for index: $growthAndDevelopmentResultIndex")
                        viewModel.onDeleteResultClick(growthAndDevelopmentResultIndex) }
            )
        }
        GrowthInfo(growthAndDevelopmentInfo = growthAndDevelopmentResult.growthAndDevelopmentInfo)
        PercentileResultSection(growthAndDevelopmentPercentiles = growthAndDevelopmentResult.growthAndDevelopmentPercentiles)
        val indexToDelete by viewModel.indexToDelete.collectAsState()
        DeleteResultDialog(showDialog = viewModel.showDialog.value,
            onConfirm = {
                Log.e("result index UI", "$indexToDelete")
                viewModel.deletePercentileResult(
                    indexToDelete
                )
            },
            onDismiss = { viewModel.onDeleteResultDialogDismiss() }
        )
    }
}

@Composable
fun GrowthInfo(growthAndDevelopmentInfo: GrowthAndDevelopmentInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .background(color = LightBlue, shape = RoundedCornerShape(4.dp))
            .height(70.dp)
    ){
        GrowthResult(labelId = R.string.height, value = growthAndDevelopmentInfo.length.toInt(), unitId = R.string.cmLabel, modifier = Modifier.weight(0.25f))
        VerticalDivider(thickness = 1.dp, color = Color.LightGray)
        GrowthResult(labelId = R.string.weight, value = growthAndDevelopmentInfo.weight.toInt(), unitId = R.string.kgLabel, modifier = Modifier.weight(0.25f))
        VerticalDivider(thickness = 1.dp, color = Color.LightGray)
        GrowthResult(labelId = R.string.age, value = growthAndDevelopmentInfo.age.toInt(), unitId = R.string.monthsLabel, modifier = Modifier.weight(0.25f))
        VerticalDivider(thickness = 1.dp, color = Color.LightGray)
        GrowthResult(labelId = R.string.headCircumference, value = growthAndDevelopmentInfo.headCircumference.toInt(), unitId = R.string.cmLabel, modifier = Modifier.weight(0.25f))
    }
}

@Composable
fun GrowthResult(
    labelId : Int,
    value : Int,
    unitId : Int,
    modifier: Modifier = Modifier
){
    Column (modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(id = labelId))
        Row (
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ){
            Text(
                text = "$value ",
                style = TextStyle(color = Blue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Text(
                text = stringResource(id = unitId),
                style = TextStyle(color = Color.Black, fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun PercentileResultSection(growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        PercentileResult(
            percentileType = R.string.lengthForAgePercentile,
            percentileValue = growthAndDevelopmentPercentiles.lengthForAgePercentile
        )
        PercentileResult(
            percentileType = R.string.weightForAgePercentile,
            percentileValue = growthAndDevelopmentPercentiles.weightForAgePercentile
        )
        PercentileResult(
            percentileType = R.string.weightForLengthPercentile,
            percentileValue = growthAndDevelopmentPercentiles.weightForLengthPercentile
        )
        PercentileResult(
            percentileType = R.string.headCircumferenceForAgePercentile,
            percentileValue = growthAndDevelopmentPercentiles.headCircumferenceForAgePercentile
        )
    }
}

@Composable
fun DeleteResultDialog(
    showDialog : Boolean,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(id = R.string.deleteCheck)) },
            text = { Text(text = stringResource(id = R.string.deleteCheckQuestion)) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(text = stringResource(id = R.string.no))
                }
            }
        )
    }
}

@Composable
fun ChoosePercentile(viewModel : NewbornHomeViewModel) {
    val selectedChartType by viewModel.selectedChartType.collectAsState()
    LazyRow (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)) {
        item {
            BasicButton(
                text = stringResource(id = R.string.lengthForAge),
                onClick = { viewModel.selectChartType("lengthForAge") },
                containerColor = if (selectedChartType == "lengthForAge") LightPink else Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicButton(
                text = stringResource(id = R.string.weightForAge),
                onClick = { viewModel.selectChartType("weightForAge")},
                containerColor = if (selectedChartType == "weightForAge") LightPink else Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicButton(
                text = stringResource(id = R.string.weightForLength),
                onClick = { viewModel.selectChartType("weightForLength")},
                containerColor = if (selectedChartType == "weightForLength") LightPink else Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicButton(
                text = stringResource(id = R.string.headCircumferenceForAge),
                onClick = { viewModel.selectChartType("headCircumferenceForAge") },
                containerColor = if (selectedChartType == "headCircumferenceForAge") LightPink else Color.White
            )
        }
    }
}
