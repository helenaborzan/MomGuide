package hr.ferit.helenaborzan.pregnancyhelper.model

data class GrowthAndDevelopmentPercentiles(
    val lengthForAgePercentile : Double = 0.0,
    val weightForAgePercentile : Double = 0.0,
    val weightForLengthPercentile : Double = 0.0,
    val headCircumferenceForAgePercentile : Double = 0.0
)
