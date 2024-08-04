package hr.ferit.helenaborzan.pregnancyhelper.repository

import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.Percentile

val heightForAgeDataGirls = listOf(
    Percentile(0, 45.4f, 48.8f, 52.1f),   // Pri rođenju
    Percentile(2, 53.4f, 57.6f, 61.9f),   // 2 mjeseca
    Percentile(4, 59.0f, 63.3f, 67.6f),   // 4 mjeseca
    Percentile(6, 63.1f, 67.5f, 71.8f),   // 6 mjeseci
    Percentile(8, 66.0f, 70.5f, 74.9f),   // 8 mjeseci
    Percentile(10, 68.5f, 73.0f, 77.5f),  // 10 mjeseci
    Percentile(12, 70.4f, 74.9f, 79.4f),  // 12 mjeseci
    Percentile(15, 73.1f, 77.7f, 82.3f),  // 15 mjeseci
    Percentile(18, 75.0f, 79.7f, 84.4f),  // 18 mjeseci
    Percentile(21, 76.9f, 81.7f, 86.5f),  // 21 mjesec
    Percentile(24, 78.6f, 83.5f, 88.3f)   // 24 mjeseca
)

val weightForAgeDataGirls = listOf(
    Percentile(0, 2.5f, 3.3f, 4.3f),    // Pri rođenju
    Percentile(2, 4.0f, 5.2f, 6.6f),    // 2 mjeseca
    Percentile(4, 5.4f, 6.8f, 8.3f),    // 4 mjeseca
    Percentile(6, 6.4f, 7.9f, 9.5f),    // 6 mjeseci
    Percentile(8, 7.0f, 8.6f, 10.4f),   // 8 mjeseci
    Percentile(10, 7.5f, 9.3f, 11.3f),  // 10 mjeseci
    Percentile(12, 8.0f, 10.0f, 12.0f), // 12 mjeseci
    Percentile(15, 8.6f, 10.6f, 12.8f), // 15 mjeseci
    Percentile(18, 9.0f, 11.1f, 13.6f), // 18 mjeseci
    Percentile(21, 9.5f, 11.7f, 14.4f), // 21 mjesec
    Percentile(24, 9.9f, 12.2f, 15.0f)  // 24 mjeseca
)

val weightForHeightDataGirls = listOf(
    Percentile(45, 2.2f, 2.5f, 2.9f),
    Percentile(50, 2.8f, 3.3f, 3.9f),
    Percentile(55, 3.6f, 4.2f, 5.0f),
    Percentile(60, 4.5f, 5.3f, 6.3f),
    Percentile(65, 5.5f, 6.5f, 7.7f),
    Percentile(70, 6.5f, 7.7f, 9.0f),
    Percentile(75, 7.6f, 8.9f, 10.5f),
    Percentile(80, 8.6f, 10.0f, 11.7f),
    Percentile(85, 9.6f, 11.3f, 13.2f),
    Percentile(90, 10.6f, 12.6f, 14.8f),
    Percentile(95, 11.6f, 13.9f, 16.2f),
    Percentile(100, 12.7f, 15.2f, 17.8f),
    Percentile(105, 13.8f, 16.5f, 20.0f),
    Percentile(110, 15.0f, 17.8f, 21.3f)
)

val headCircumferenceForAgeDataGirls = listOf(
    Percentile(0, 32.0f, 34.2f, 36.4f),   // Pri rođenju
    Percentile(2, 35.0f, 37.2f, 39.5f),   // 2 mjeseca
    Percentile(4, 37.0f, 39.3f, 41.6f),   // 4 mjeseca
    Percentile(6, 38.5f, 40.7f, 43.0f),   // 6 mjeseci
    Percentile(8, 39.5f, 41.6f, 43.9f),   // 8 mjeseci
    Percentile(10, 40.0f, 42.2f, 44.4f),  // 10 mjeseci
    Percentile(12, 40.6f, 42.9f, 45.1f),  // 12 mjeseci
    Percentile(15, 41.2f, 43.4f, 45.6f),  // 15 mjeseci
    Percentile(18, 41.8f, 44.1f, 46.4f),  // 18 mjeseci
    Percentile(21, 42.2f, 44.5f, 46.8f),  // 21 mjesec
    Percentile(24, 42.6f, 44.9f, 47.2f)   // 24 mjeseca
)
