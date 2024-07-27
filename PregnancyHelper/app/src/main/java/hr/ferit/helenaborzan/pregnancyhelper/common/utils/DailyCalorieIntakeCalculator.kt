package hr.ferit.helenaborzan.pregnancyhelper.common.utils

import hr.ferit.helenaborzan.pregnancyhelper.model.ActivityLevel


fun calculateBMR(weightKg: Double, heightCm: Double, age: Int): Double {
    return 655 + (9.6 * weightKg) + (1.8 * heightCm) - (4.7 * age)
}

fun calculateTDEE(weightKg: Double, heightCm: Double, age: Int, activityLevel: ActivityLevel): Double {
    val bmr = calculateBMR(weightKg = weightKg, heightCm = heightCm, age = age)
    return bmr * activityLevel.multiplier
}