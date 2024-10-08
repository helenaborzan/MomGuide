package hr.ferit.helenaborzan.pregnancyhelper.screens.login

import androidx.annotation.StringRes

data class LoginUiState(
    val email : String = "",
    val password : String = "",
    val errorMessage: Int? = null,
    val isLoginSuccessful : Boolean = false
)
