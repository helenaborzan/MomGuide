package hr.ferit.helenaborzan.pregnancyhelper.screens.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.isValidEmail
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.isValidPassword
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.passwordMatches
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.screens.PregnancyHelperViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val accountService: AccountService
) : PregnancyHelperViewModel(){

    var uiState =  mutableStateOf(RegistrationUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password
    private val repeatPassword
        get() = uiState.value.repeatPassword

    fun onEmailChange (newValue : String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange (newValue : String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange (newValue : String){
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }
    fun checkIfAccountExists(email: String, onResult: (Boolean) -> Unit) {
        Log.d("RegistrationViewModel", "Checking if account exists for email: $email")
        launchCatching {
            try {
                val accountExists = accountService.checkIfAccountExists(email)
                Log.d("RegistrationViewModel", "Account exists check result: $accountExists")
                onResult(accountExists)
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "Error checking if account exists", e)
                onResult(false)
            }
        }
    }

    fun onSignUpClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                Log.d("RegistrationViewModel", "Starting registration process for email: $email")
                val accountExists = accountService.checkIfAccountExists(email)
                Log.d("RegistrationViewModel", "Account exists check result: $accountExists")

                if (accountExists) {
                    Log.d("RegistrationViewModel", "Account already exists, showing error")
                    uiState.value = uiState.value.copy(
                        errorMessage = R.string.accountAlreadyExistsError,
                        isRegistrationSuccessful = false
                    )
                } else {
                    Log.d("RegistrationViewModel", "Account does not exist, proceeding with registration")
                    registerAccount()
                }
            } catch (e: IllegalArgumentException) {
                Log.e("RegistrationViewModel", "Invalid email format", e)
                uiState.value = uiState.value.copy(
                    errorMessage = R.string.invalidEmailError,
                    isRegistrationSuccessful = false
                )
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "Error during registration process", e)
                uiState.value = uiState.value.copy(
                    errorMessage = R.string.registrationError,
                    isRegistrationSuccessful = false
                )
            }
        }
    }

    fun clearError(){
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    private fun validateInputs(): Boolean {
        return when {
            !email.isValidEmail() -> {
                uiState.value = uiState.value.copy(errorMessage = R.string.emailError)
                false
            }
            !password.isValidPassword() -> {
                uiState.value = uiState.value.copy(errorMessage = R.string.passwordError)
                false
            }
            !password.passwordMatches(repeatPassword) -> {
                uiState.value = uiState.value.copy(errorMessage = R.string.repeatPasswordError)
                false
            }
            else -> true
        }
    }

    private fun registerAccount() {
        Log.d("RegistrationViewModel", "Attempting to register account")
        launchCatching {
            try {
                accountService.signIn(email = email, password = password)
                Log.d("RegistrationViewModel", "Registration successful")
                uiState.value = uiState.value.copy(isRegistrationSuccessful = true)
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "Error during registration", e)
                uiState.value = uiState.value.copy(
                    errorMessage = R.string.registrationError,
                    isRegistrationSuccessful = false
                )
            }
        }
    }
}