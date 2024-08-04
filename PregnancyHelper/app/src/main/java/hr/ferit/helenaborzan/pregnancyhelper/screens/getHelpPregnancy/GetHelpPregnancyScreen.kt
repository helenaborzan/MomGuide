package hr.ferit.helenaborzan.pregnancyhelper.screens.getHelpPregnancy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.screens.map.MapSection
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray

@RequiresApi(34)
@Composable
fun GetHelpPregnancyScreen(navController: NavController) {
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
    ){
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            navController = navController
        )
        Column (modifier = Modifier.weight(0.75f)){
            MapSection()
        }
        HelpDescription(modifier = Modifier.weight(0.15f))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HelpDescription(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(id = R.string.pregnancyHelp),
            style = TextStyle(color = DarkGray),
            textAlign = TextAlign.Center
        )
    }
}