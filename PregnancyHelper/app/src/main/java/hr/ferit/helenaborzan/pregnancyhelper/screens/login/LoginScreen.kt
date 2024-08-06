package hr.ferit.helenaborzan.pregnancyhelper.screens.login

import android.R.attr.value
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ButtonWithGradient
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.EmailTextField
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.PasswordTextField
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightPink),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TitleBar(modifier = Modifier
            .fillMaxWidth()
            .weight(0.75f))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .clip(RoundedCornerShape(80.dp, 80.dp, 0.dp, 0.dp))
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            UserInfoInput(modifier = Modifier.weight(2f),uiState = uiState, viewModel = viewModel, navController = navController)
            Column (modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                ButtonWithGradient(text = stringResource(id = R.string.login), onClick = {
                    viewModel.onLoginClick()
                    }
                )
            }
            LaunchedEffect(viewModel.uiState.value.isLoginSuccessful) {
                if (viewModel.uiState.value.isLoginSuccessful) {
                    navController.navigate(Screen.ChooseCategoryScreen.route)
                }
            }
        }

    }
    LoginErrorDialog(viewModel = viewModel, uiState = uiState)
}

@Composable
fun TitleBar(modifier : Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ){
        Text(
            text = stringResource(id = R.string.loginButtonText),
            style = TextStyle(color = DarkGray, fontSize = 28.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 32.dp)
        )
    }
}

@Composable
fun UserInfoInput(
    modifier : Modifier = Modifier,
    uiState : LoginUiState,
    viewModel : LoginViewModel,
    navController: NavController
) {
    val focusRequester = remember { FocusRequester() }
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ){
        EmailTextField(
            label = stringResource(id = R.string.emailLabel),
            value = uiState.email,
            icon = R.drawable.baseline_email_24,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusRequester.requestFocus()
            }),
            onValueChange = viewModel::onEmailChange
        )
        Spacer(modifier = Modifier.size(16.dp))
        PasswordTextField(
            label = stringResource(id = R.string.passwordLabel),
            value = uiState.password,
            icon = R.drawable.baseline_lock_24,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {}),
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.focusRequester(focusRequester)
        )
        UserHasNoAccount(navController = navController)
    }
}

@Composable
fun UserHasNoAccount(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.goToRegistration),
            style = TextStyle(
                color = DarkGray,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .padding(24.dp)
                .clickable { navController.navigate(Screen.RegistrationScreen.route) },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginErrorDialog(
    viewModel : LoginViewModel,
    uiState : LoginUiState
) {
    uiState.errorMessage?.let { messageId ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(id = R.string.error)) },
            text = { Text(stringResource(id = messageId)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(Pink)
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}






