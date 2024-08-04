package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment

import androidx.annotation.StringRes
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentPercentiles

data class GrowthAndDevelopmentCalculationUiState(
    val growthAndDevelopmentInfo: GrowthAndDevelopmentInfo = GrowthAndDevelopmentInfo(),
    val growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles(),
    @StringRes val errorMessageResource : Int? = null,
)