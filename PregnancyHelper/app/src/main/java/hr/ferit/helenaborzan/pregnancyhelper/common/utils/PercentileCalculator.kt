package hr.ferit.helenaborzan.pregnancyhelper.common.utils

import androidx.annotation.StringRes
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.repository.LMSTableRepository
import org.apache.commons.math3.distribution.NormalDistribution
import java.lang.Math.pow
import javax.inject.Inject
import kotlin.math.round

class PercentileCalculator @Inject constructor(
    private val LMSTableRepository : LMSTableRepository
) {
    fun <T>calculateZvalue(type : String, sex : String, searchCriteria : T, value : Int) : Double{
        val LMS_values = LMSTableRepository.getLMS(type = type, sex = sex, searchCriteria = searchCriteria)
        if (LMS_values != null) {
            val L = LMS_values[0]
            val M = LMS_values[1]
            val S = LMS_values[2]
            return (pow((value / M), L) - 1) / (L * S)
        }
        return 0.0
    }

    fun <T>calculatePercentile(type : String, sex : String, searchCriteria : T, value : Int) : Double{
        val z_value = calculateZvalue(type = type, sex = sex, searchCriteria = searchCriteria, value = value)
        val normalDistribution = NormalDistribution()
        return String.format("%.3f", normalDistribution.cumulativeProbability(z_value) * 100).toDouble()
    }

    fun isPercentileInNormalLimits(percentileValue : Double) : Boolean{
        val lowerLimit = 3.0
        val upperLimit = 97.0

        return percentileValue >= lowerLimit && percentileValue <= upperLimit
    }
}