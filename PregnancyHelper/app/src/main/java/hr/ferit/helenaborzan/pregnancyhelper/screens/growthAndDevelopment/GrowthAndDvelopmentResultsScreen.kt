package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentResult
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink


@Composable
fun GrowthAndDevelopmentResultsScreen(
    viewModel: NewbornHomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())
    val growthAndDevelopmentResults = remember(newbornInfo) {
        newbornInfo.flatMap { it.growthAndDevelopmentResults }
    }
    Column (modifier = Modifier.background(color = DirtyWhite)){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        LazyColumn(modifier = Modifier
            .padding(24.dp)
            .weight(0.9f)) {
            items(growthAndDevelopmentResults) { result ->
                PercentilesResult(growthAndDevelopmentResult = result)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }
}

@Composable
fun PercentilesResult(growthAndDevelopmentResult: GrowthAndDevelopmentResult) {
    val date = growthAndDevelopmentResult.growthAndDevelopmentInfo.date
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.border(width = 1.dp, color = Pink, shape = RoundedCornerShape(4.dp))
    ){
        if (date != null) {
            val dateYMD = getDate(date)
            Text(
                text = "${dateYMD.getValue("day")}.${dateYMD.getValue("month")}.${dateYMD.getValue("year")}.",
                style = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 0.dp, end = 12.dp)
            )
        }
            PercentileResultSection(growthAndDevelopmentPercentiles = growthAndDevelopmentResult.growthAndDevelopmentPercentiles)

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