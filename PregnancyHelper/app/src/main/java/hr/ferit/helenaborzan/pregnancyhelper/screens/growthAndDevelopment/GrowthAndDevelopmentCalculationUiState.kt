package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import androidx.annotation.StringRes
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.GrowthAndDevelopmentPercentiles

data class GrowthAndDevelopmentCalculationUiState(
    val growthAndDevelopmentInfo: GrowthAndDevelopmentInfo = GrowthAndDevelopmentInfo(),
    val growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles(),
    @StringRes val errorMessageResource : Int? = null,
    val isRadioButtonChecked : Boolean = false
)