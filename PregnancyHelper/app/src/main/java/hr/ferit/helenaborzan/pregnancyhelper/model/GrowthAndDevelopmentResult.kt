package hr.ferit.helenaborzan.pregnancyhelper.model

import com.google.firebase.Timestamp

data class GrowthAndDevelopmentResult(
    val growthAndDevelopmentInfo: GrowthAndDevelopmentInfo = GrowthAndDevelopmentInfo(),
    val growthAndDevelopmentPercentiles: GrowthAndDevelopmentPercentiles = GrowthAndDevelopmentPercentiles()
)
