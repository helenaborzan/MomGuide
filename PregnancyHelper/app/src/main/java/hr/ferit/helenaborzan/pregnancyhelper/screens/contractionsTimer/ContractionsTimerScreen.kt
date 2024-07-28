package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatDuration
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatStartTime
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ContractionsTimerScreen(
    navController: NavController,
    viewModel : ContractionsTimerViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    var shouldGoToTheHospital by remember { mutableStateOf(false) }


    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = DirtyWhite)
    ){
        LaunchedEffect(uiState){
            shouldGoToTheHospital = viewModel.shouldGoToTheHospital(uiState)
        }
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(0.1f),
            navController = navController
        )
        ContractionButton(modifier = Modifier.weight(0.4f, fill = false), uiState = uiState, viewModel = viewModel)
        Spacer(modifier = Modifier.height(40.dp))
        AverageResults(modifier = Modifier.weight(0.1f), uiState = uiState)
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        TimedContractions(modifier = Modifier.weight(0.3f), uiState = uiState)
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        if (shouldGoToTheHospital){
            HospitalRecomendation()
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContractionButton(
    modifier: Modifier = Modifier,
    uiState: ContractionsTimerUiState,
    viewModel: ContractionsTimerViewModel
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val animatedModifier = if (uiState.isTimerRunning) {
        modifier.scale(scale)
    } else {
        modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = animatedModifier
            .size(300.dp)
            .border(
                width = 10.dp,
                brush = if (uiState.isTimerRunning)
                    Brush.linearGradient(0.2f to LilaPink, 1.0f to Purple)
                else
                    Brush.linearGradient(0.2f to Color.LightGray, 1.0f to Blue),
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AverageResults(modifier: Modifier = Modifier, uiState: ContractionsTimerUiState) {
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(24.dp)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
    ){
        AverageResult(label = stringResource(id = R.string.numberOfContractions),
            value = uiState.contractions.size,
            modifier = Modifier.weight(0.3f))
        AverageResult(label = stringResource(id = R.string.averageContractionDuration),
            value = formatDuration(uiState.averageContractionDuration),
            modifier = Modifier.weight(0.3f))
        AverageResult(label = stringResource(id = R.string.averageContractionFrequency),
            value = formatDuration(uiState.averageContractionFrequency),
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
                ContractionInfo(
                    contractionsInfo = uiState.contractions[index],
                    frequency = if (index > 0) uiState.frequencies.getOrNull(index - 1) else null
                )
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
fun ContractionInfo(contractionsInfo: ContractionsInfo, frequency: Duration?) {
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
            text = frequency?.let { formatDuration(it) } ?: "-",
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

@Composable
fun HospitalRecomendation() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "🚑",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.weight(0.3f)
        )
        Column (modifier = Modifier.weight(0.7f)){
            Text(
                text = stringResource(id = R.string.hospitalRecomendation),
                style = TextStyle(color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(id = R.string.hospitalRecomendationExplanation),
                style = TextStyle(color = DarkGray, fontSize = 16.sp)
            )
        }
    }
}
