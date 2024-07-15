package hr.ferit.helenaborzan.pregnancyhelper.repository


import hr.ferit.helenaborzan.pregnancyhelper.model.Percentile

val heightForAgeData = listOf(
    Percentile(0, 46.1f, 49.9f, 53.7f),   // Pri rođenju
    Percentile(2, 54.4f, 58.4f, 62.4f),   // 2 mjeseca
    Percentile(4, 60.0f, 64.0f, 68.0f),   // 4 mjeseca
    Percentile(6, 64.0f, 68.0f, 72.0f),   // 6 mjeseci
    Percentile(8, 67.0f, 71.0f, 75.0f),   // 8 mjeseci
    Percentile(10, 69.5f, 73.5f, 77.5f),  // 10 mjeseci
    Percentile(12, 71.5f, 75.7f, 79.9f),  // 12 mjeseci
    Percentile(15, 74.5f, 78.9f, 83.3f),  // 15 mjeseci
    Percentile(18, 77.0f, 81.7f, 86.4f),  // 18 mjeseci
    Percentile(21, 79.3f, 84.2f, 89.1f),  // 21 mjesec
    Percentile(24, 81.2f, 86.4f, 91.6f)   // 24 mjeseca
)

val weightForAgeData = listOf(
    Percentile(0, 2.5f, 3.3f, 4.3f),    // Pri rođenju
    Percentile(2, 4.3f, 5.6f, 7.1f),    // 2 mjeseca
    Percentile(4, 5.6f, 7.0f, 8.6f),    // 4 mjeseca
    Percentile(6, 6.4f, 7.9f, 9.7f),    // 6 mjeseci
    Percentile(8, 7.0f, 8.6f, 10.5f),   // 8 mjeseci
    Percentile(10, 7.4f, 9.2f, 11.2f),  // 10 mjeseci
    Percentile(12, 7.8f, 9.6f, 11.8f),  // 12 mjeseci
    Percentile(15, 8.4f, 10.3f, 12.6f), // 15 mjeseci
    Percentile(18, 8.9f, 10.9f, 13.4f), // 18 mjeseci
    Percentile(21, 9.3f, 11.5f, 14.1f), // 21 mjesec
    Percentile(24, 9.7f, 12.0f, 14.8f)  // 24 mjeseca
)

val weightForHeightData = listOf(
    Percentile(45, 2.2f, 2.5f, 2.9f),
    Percentile(50, 2.9f, 3.4f, 4.0f),
    Percentile(55, 3.8f, 4.4f, 5.2f),
    Percentile(60, 4.8f, 5.6f, 6.6f),
    Percentile(65, 5.9f, 6.9f, 8.1f),
    Percentile(70, 7.1f, 8.2f, 9.7f),
    Percentile(75, 8.2f, 9.5f, 11.2f),
    Percentile(80, 9.3f, 10.8f, 12.7f),
    Percentile(85, 10.4f, 12.1f, 14.2f),
    Percentile(90, 11.5f, 13.4f, 15.8f),
    Percentile(95, 12.6f, 14.7f, 17.4f),
    Percentile(100, 13.8f, 16.1f, 19.1f),
    Percentile(105, 15.0f, 17.5f, 20.8f),
    Percentile(110, 16.3f, 19.0f, 22.6f)
)


val headCircumferenceForAgeData = listOf(
    Percentile(0, 32.1f, 34.5f, 36.9f),   // Pri rođenju
    Percentile(2, 36.0f, 38.3f, 40.6f),   // 2 mjeseca
    Percentile(4, 38.5f, 40.8f, 43.1f),   // 4 mjeseca
    Percentile(6, 40.2f, 42.4f, 44.6f),   // 6 mjeseci
    Percentile(8, 41.4f, 43.5f, 45.6f),   // 8 mjeseci
    Percentile(10, 42.2f, 44.3f, 46.4f),  // 10 mjeseci
    Percentile(12, 42.9f, 44.9f, 46.9f),  // 12 mjeseci
    Percentile(15, 43.8f, 45.8f, 47.8f),  // 15 mjeseci
    Percentile(18, 44.4f, 46.3f, 48.2f),  // 18 mjeseci
    Percentile(21, 44.9f, 46.8f, 48.7f),  // 21 mjesec
    Percentile(24, 45.3f, 47.2f, 49.1f)   // 24 mjeseca
)