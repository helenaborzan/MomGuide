package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen

@Composable
fun GoBackIconBar(
    modifier : Modifier = Modifier,
    navController : NavController) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
            contentDescription = "Go back",
            modifier = Modifier.clickable { navController.popBackStack() }
        )
    }
}

@Composable
fun NewbornHomeIconBar(
    modifier : Modifier = Modifier,
    navController : NavController
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_home_24),
            contentDescription = "Go to home",
            modifier = Modifier.clickable { navController.navigate(Screen.NewbornHomeScreen.route) }
        )
    }
}