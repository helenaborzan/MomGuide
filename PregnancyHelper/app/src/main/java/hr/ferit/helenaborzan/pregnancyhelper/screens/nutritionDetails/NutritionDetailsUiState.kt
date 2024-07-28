package hr.ferit.helenaborzan.pregnancyhelper.screens.nutritionDetails

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
data class NutritionDetailsUiState(
    val selectedDate : LocalDate = LocalDate.now(),
)
