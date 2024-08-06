package hr.ferit.helenaborzan.pregnancyhelper.screens.contractionsTimer

import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.model.data.contractions.ContractionsInfo
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DirtyWhite
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LilaPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Purple
import java.time.Duration
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatDuration
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.formatStartTime
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Blue
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Green
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Red


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ContractionsTimerScreen(
    navController: NavController,
    viewModel : ContractionsTimerViewModel = hiltViewModel()
){

    val uiState by viewModel.uiState.collectAsState()
    var shouldGoToTheHospital by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }


    LaunchedEffect(uiState){
        shouldGoToTheHospital = viewModel.shouldGoToTheHospital(uiState)
    }
    LaunchedEffect(uiState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && viewModel.shouldPromptForNotifications(uiState)) {
            if (!hasNotificationPermission) {
                notificationPermissionState?.launchPermissionRequest()
            }
        }
    }

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
                .padding(24.dp),
            navController = navController
        )
        ContractionButton(uiState = uiState, viewModel = viewModel)
        Spacer(modifier = Modifier.height(20.dp))
        AverageResults( uiState = uiState)
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        TimedContractions(uiState = uiState)
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {

            if (shouldGoToTheHospital) {
                GoToHospital()
            } else {
                DontGoToHospital()
            }
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
            .size(150.dp)
            .border(
                width = 6.dp,
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
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
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
            .height(90.dp)
            .padding(bottom = 0.dp, start = 4.dp, top = 4.dp, end = 4.dp)
    ){
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontSize = 10.sp
        )
        Text(
            text = "$value",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 10.sp
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimedContractions(modifier: Modifier = Modifier, uiState: ContractionsTimerUiState) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.contractions) {
        if (uiState.contractions.isNotEmpty()) {
            listState.animateScrollToItem(uiState.contractions.size - 1)
        }
    }
    Column(modifier = modifier
        .fillMaxWidth()
        .height(200.dp)) {
        TimedContractionsLabels()
        LazyColumn(state = listState) {
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
fun GoToHospital() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "🚑",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.width(32.dp))
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(id = R.string.hospitalRecomendation),
                style = TextStyle(color = Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(id = R.string.hospitalRecomendationExplanation),
                style = TextStyle(color = DarkGray, fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun DontGoToHospital() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(id = R.string.dontGoToHospital),
                style = TextStyle(color = Green, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.dontGoToHospitalExplanation),
                style = TextStyle(color = DarkGray, fontSize = 12.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}


