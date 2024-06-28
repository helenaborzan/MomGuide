package hr.ferit.helenaborzan.pregnancyhelper.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.isValidEmail

import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.screens.PregnancyHelperViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
)
    : PregnancyHelperViewModel(){
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password


    fun onEmailChange(newValue : String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue : String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onLoginClick() {
        if (!email.isValidEmail()) {
            uiState.value = uiState.value.copy(errorMessage = R.string.emailError)
            return
        }

        if (password.isBlank()) {
            uiState.value = uiState.value.copy(errorMessage = R.string.emptyPasswordError)
            return
        }

        launchCatching {
            try {
                accountService.authenticate(email, password)
                uiState.value = uiState.value.copy(isLoginSuccessful = true)
            } catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = R.string.authenticationError, isLoginSuccessful = false)
            }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

}