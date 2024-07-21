package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getHoursAndMins
import hr.ferit.helenaborzan.pregnancyhelper.model.BottleInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.BreastfeedingInfo
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.screens.newbornHome.NewbornHomeViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import io.data2viz.charts.core.Padding

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingInfoScreen(
    viewModel : NewbornHomeViewModel = hiltViewModel(),
    navController : NavController
){
    val newbornInfo by viewModel.newbornInfo.collectAsState(initial = emptyList())
    val breastfeedingInfo = remember(newbornInfo) {
        newbornInfo.flatMap { it.breastfeedingInfo }
    }
    val bottleInfo = remember(newbornInfo) {
        newbornInfo.flatMap { it.bottleInfo }
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        BreastfeedingHistory(
            breastfeedingInfo = breastfeedingInfo,
            modifier = Modifier.weight(0.9f))
    }
    LaunchedEffect(Unit) {
        viewModel.getUsersNewbornInfo()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreastfeedingHistory(
    modifier: Modifier = Modifier,
    breastfeedingInfo : List<BreastfeedingInfo>) {
    LazyColumn(
        modifier = modifier)
    { items(breastfeedingInfo.size){
        val startHours = getHoursAndMins(breastfeedingInfo[it].startTime)?.get("hours") ?: 0
        val startMins = getHoursAndMins(breastfeedingInfo[it].startTime)?.get("minutes") ?: 0
        val endHours = getHoursAndMins(breastfeedingInfo[it].endTime)?.get("hours") ?: 0
        val endMins = getHoursAndMins(breastfeedingInfo[it].endTime)?.get("minutes") ?: 0
        BreastfeedingCard(
            text = "$startHours:$startMins - $endHours:$endMins, ${breastfeedingInfo[it].breast}"
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottleHistory(
    modifier: Modifier = Modifier,
    bottleInfo : List<BottleInfo>
) {
    LazyColumn(
        modifier = modifier)
    { items(bottleInfo.size){
        val hours = getHoursAndMins(bottleInfo[it].time)?.get("hours") ?: 0
        val mins = getHoursAndMins(bottleInfo[it].time)?.get("minutes") ?: 0
        BreastfeedingCard(
            text = "$hours:$mins, ${bottleInfo[it].quantity}ml"
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
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