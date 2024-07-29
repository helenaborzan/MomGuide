package hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment

import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentInfo
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.GrowthAndDevelopmentPercentiles

data class GrowthAndDevelopmentResult(
    val growthAndDevelopmentInfo: GrowthAndDevelopmentInfo = GrowthAndDevelopmentInfo(),
    val growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles()
)
