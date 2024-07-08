package hr.ferit.helenaborzan.pregnancyhelper.screens.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.ButtonWithGradient
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.EmailTextField
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.PasswordTextField
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginViewModel
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    navController: NavController
) {
    val focusRequester = remember { FocusRequester() }
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
            UserInfoInput(modifier = Modifier.weight(2f), viewModel = viewModel, uiState = uiState, navController = navController)
            Column (modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                ButtonWithGradient(text = stringResource(id = R.string.register), onClick = {
                    viewModel.onSignUpClick()
                })
            }
            LaunchedEffect(viewModel.uiState.value.isRegistrationSuccessful) {
                if (viewModel.uiState.value.isRegistrationSuccessful) {
                    navController.navigate(Screen.ChooseCategoryScreen.route)
                }
            }
        }

    }
    RegistrationErrorDialog(uiState = uiState, viewModel = viewModel)
}

@Composable
fun TitleBar(modifier : Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ){
        Text(
            text = stringResource(id = R.string.registerButtonText),
            style = TextStyle(color = DarkGray, fontSize = 28.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 28.dp)
        )
    }
}

@Composable
fun UserInfoInput(
    modifier : Modifier = Modifier,
    viewModel: RegistrationViewModel,
    uiState : RegistrationUiState,
    navController: NavController
) {
    val focusRequester = remember { FocusRequester() }
    Column (
        modifier = modifier,
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
        Spacer(modifier = Modifier.size(8.dp))
        PasswordTextField(
            label = stringResource(id = R.string.passwordLabel),
            value = uiState.password,
            icon = R.drawable.baseline_lock_24,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {}),
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.size(8.dp))
        PasswordTextField(
            label = stringResource(R.string.repeatPassword),
            value = uiState.repeatPassword,
            icon = R.drawable.baseline_lock_24,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {}),
            onValueChange = viewModel::onRepeatPasswordChange,
            modifier = Modifier.focusRequester(focusRequester)
        )
        UserAlreadyHasAccount(navController = navController)
    }
}

@Composable
fun UserAlreadyHasAccount(navController : NavController) {

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.goToLogin),
            style = TextStyle(
                color = DarkGray,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center)
                .clickable { navController.navigate(Screen.LoginScreen.route) }
        )
    }

}

@Composable
fun RegistrationErrorDialog(uiState : RegistrationUiState, viewModel: RegistrationViewModel) {
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