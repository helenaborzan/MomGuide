package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getHoursAndMins
import hr.ferit.helenaborzan.pregnancyhelper.model.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.navigation.NavigationController
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LilaPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Purple
import java.time.Duration
import java.time.Instant
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatDuration
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatStartTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContractionsTimerScreen(
    navController: NavController,
    viewModel : ContractionsTimerViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = DirtyWhite)
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        ContractionButton(modifier = Modifier.weight(0.4f, fill = false), uiState = uiState, viewModel = viewModel)
        Spacer(modifier = Modifier.height(40.dp))
        AverageResults(modifier = Modifier.weight(0.1f))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        TimedContractions(modifier = Modifier.weight(0.3f), uiState = uiState)
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContractionButton(
    modifier : Modifier = Modifier,
    uiState: ContractionsTimerUiState,
    viewModel: ContractionsTimerViewModel) {
    Box(
        contentAlignment= Alignment.Center,
        modifier = modifier
            .size(300.dp)
            .border(
                width = 10.dp,
                brush = Brush.linearGradient(0.2f to LilaPink, 1.0f to Purple),
                shape = CircleShape
            )
            .clip(shape = CircleShape)
            .background(color = LightestPink)
            .clickable { viewModel.onContractionsButtonClick() }
    ) {
        Text(
            text = if (uiState.isTimerRunning) stringResource(id = R.string.contractionEnded)
                    else stringResource(id = R.string.contractionStarted),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun AverageResults(modifier: Modifier = Modifier) {
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(24.dp)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
    ){
        AverageResult(label = stringResource(id = R.string.numberOfContractionsInHour),
            value = 3,
            modifier = Modifier.weight(0.3f))
        AverageResult(label = stringResource(id = R.string.averageContractionDuration),
            value = 4.3,
            modifier = Modifier.weight(0.3f))
        AverageResult(label = stringResource(id = R.string.averageContractionFrequency),
            value = 5.4,
            modifier = Modifier.weight(0.3f))
    }
}

@Composable
fun <T>AverageResult(label : String, value : T, modifier : Modifier = Modifier) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height(60.dp)
            .padding(bottom = 0.dp, start = 4.dp, top = 4.dp, end = 4.dp)
    ){
        Text(
            text = label,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$value",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimedContractions(modifier: Modifier = Modifier, uiState: ContractionsTimerUiState) {
    Column(modifier = modifier.fillMaxWidth()) {
        TimedContractionsLabels()
        LazyColumn() {
            items(uiState.contractions.size) { index ->
                ContractionInfo(contractionsInfo = uiState.contractions[index])
            }
        }
    }
}

@Composable
fun TimedContractionsLabels() {
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ){
        TextLabel(
            text = stringResource(id = R.string.start),
            modifier = Modifier.weight(0.3f)
        )
        TextLabel(
            text = stringResource(id = R.string.duration),
            modifier = Modifier.weight(0.3f)
            )
        TextLabel(
            text = stringResource(id = R.string.frequency),
            modifier = Modifier.weight(0.3f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContractionInfo(contractionsInfo: ContractionsInfo) {
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ){
        TextLabel(
            text = "${formatStartTime(contractionsInfo.startTime)}",
            modifier = Modifier.weight(0.3f)
        )
        TextLabel(
            text = "${formatDuration(contractionsInfo.duration)}",
            modifier = Modifier.weight(0.3f)
        )
        TextLabel(
            text = "${formatDuration(contractionsInfo.frequency)}",
            modifier = Modifier.weight(0.3f)
        )

    }
}

@Composable
fun TextLabel(text : String, modifier : Modifier  = Modifier) {
    Text(
        text = text,
        style = TextStyle(color = DarkGray),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
