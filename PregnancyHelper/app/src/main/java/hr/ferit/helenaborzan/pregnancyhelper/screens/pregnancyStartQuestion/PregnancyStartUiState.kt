package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyStartQuestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
data class PregnancyStartUiState(
    val pregnancyStartDate : LocalDate = LocalDate.now(),
    @StringRes val errorMessageResource : Int? = null
)
