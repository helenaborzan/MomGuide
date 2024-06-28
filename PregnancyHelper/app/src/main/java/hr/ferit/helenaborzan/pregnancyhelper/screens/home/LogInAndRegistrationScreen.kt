package hr.ferit.helenaborzan.pregnancyhelper.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.BasicButton
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ButtonWithGradient
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import javax.inject.Inject



@Composable
fun LoginAndRegistrationScreen (
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(20.dp)
    ) {
        TitleBar()
        Spacer(modifier = Modifier.height(40.dp))
        Description()
        Spacer(modifier = Modifier.height(40.dp))
        Image(painterResource(id = R.drawable.pregnancy), contentDescription = null, modifier = Modifier.size(width = 220.dp, height = 220.dp))
        Spacer(modifier = Modifier.height(60.dp))
        BasicButton(text = stringResource(id = R.string.loginButtonText), onClick={
            navController.navigate(Screen.LoginScreen.route)
        })
        Spacer(modifier = Modifier.height(20.dp))
        ButtonWithGradient(text = stringResource(id = R.string.registerButtonText), onClick = {
            navController.navigate(Screen.RegistrationScreen.route)
        })

    }
}

@Composable
fun TitleBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = stringResource(id = R.string.loginAndRegistrationTitle),
            style = TextStyle(color = DarkGray, fontSize = 32.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}


@Composable
fun Description(){
    Text(
        text = stringResource(id = R.string.loginAndRegistrationDescription),
        style = TextStyle(color = Pink, fontSize=16.sp, fontFamily = FontFamily.SansSerif)
    )
}






