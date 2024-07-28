package hr.ferit.helenaborzan.pregnancyhelper.screens.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.isValidEmail
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.isValidPassword
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.passwordMatches
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.screens.PregnancyHelperViewModel
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

    fun onSignUpClick(){
        if (!email.isValidEmail()){
            uiState.value = uiState.value.copy(errorMessage = R.string.emailError)
            return
        }
        if(!password.isValidPassword()){
            uiState.value = uiState.value.copy(errorMessage = R.string.passwordError)
            return
        }

        if(!password.passwordMatches(repeatPassword)){
            uiState.value = uiState.value.copy(errorMessage = R.string.repeatPasswordError)
            return
        }

        launchCatching {
            try {
                val accountExists = accountService.checkIfAccountExists(email)
                if (accountExists) {
                    uiState.value = uiState.value.copy(
                        errorMessage = R.string.accountAlreadyExistsError,
                        isRegistrationSuccessful = false)
                }
                else {
                    accountService.signIn(email = email, password = password)
                    uiState.value = uiState.value.copy(isRegistrationSuccessful = true)
                }
            }catch (e : Exception){
                Log.e("RegistrationViewModel", "Error during registration", e)
                uiState.value = uiState.value.copy(errorMessage = R.string.registrationError,
                    isRegistrationSuccessful = false
                )
            }
        }
    }
    fun clearError(){
        uiState.value = uiState.value.copy(errorMessage = null)
    }
}