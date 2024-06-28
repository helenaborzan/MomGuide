package hr.ferit.helenaborzan.pregnancyhelper.screens.registration

data class RegistrationUiState(
    val email : String = "",
    val password : String = "",
    val repeatPassword : String = "",
    val errorMessage : Int? = null,
    val isRegistrationSuccessful : Boolean = false
)
