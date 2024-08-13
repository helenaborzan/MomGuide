package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import hr.ferit.helenaborzan.pregnancyhelper.common.ext.getDate


@Composable
fun BarChart(dates: List<Timestamp>, results: List<String>, categories: List<String>, barColor: Color) {
    val chartHeight = 160f
    val maxBarWidth = 60f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val xAxisY = size.height - 40f
            val yAxisX = 120f
            val chartWidth = size.width - yAxisX - 20f
            val yAxisTop = 30f  // Space for the top label

            val barCount = dates.size
            val totalPaddingWidth = chartWidth * 0.2f
            val paddingBetweenBars = totalPaddingWidth / (barCount + 1)

            // Calculate bar width with a maximum limit
            val calculatedBarWidth = (chartWidth - totalPaddingWidth) / barCount
            val barWidth = minOf(calculatedBarWidth, maxBarWidth)

            // Recalculate total width of all bars
            val totalBarWidth = barWidth * barCount

            // Calculate the starting X position to center the bars
            val startX = yAxisX + (chartWidth - totalBarWidth - (barCount - 1) * paddingBetweenBars) / 2

            // Draw x-axis
            drawLine(
                Color.Black,
                Offset(yAxisX, xAxisY),
                Offset(size.width - 20f, xAxisY),
                strokeWidth = 2f
            )

            // Draw y-axis
            drawLine(
                Color.Black,
                Offset(yAxisX, xAxisY),
                Offset(yAxisX, yAxisTop),
                strokeWidth = 2f
            )

            // Calculate y-axis label positions
            val yAxisLabelPositions = List(categories.size) { index ->
                xAxisY - (index + 1).toFloat() / (categories.size + 1) * (xAxisY - yAxisTop)
            }
            val xAxisCenter = yAxisX + chartWidth / 2

            // Draw bars
            results.forEachIndexed { index, result ->
                val categoryIndex = categories.indexOf(result)
                if (categoryIndex != -1) {
                    val barHeight = xAxisY - yAxisLabelPositions[categoryIndex]
                    val barX = if (results.size == 1) {
                        // If there's only one bar, center it on the x-axis
                        xAxisCenter - barWidth / 2
                    } else {
                        // Otherwise, use the adjusted calculation from before
                        startX + (index + 0.5f) * (barWidth + paddingBetweenBars) - barWidth / 2
                    }
                    drawRect(
                        barColor,
                        topLeft = Offset(barX, xAxisY - barHeight),
                        size = Size(barWidth, barHeight),
                    )
                }
            }

            // Draw x-axis labels (dates)
            dates.forEachIndexed { index, date ->
                val labelX = if (dates.size == 1) {
                    // If there's only one date, center it on the x-axis
                    xAxisCenter
                } else {
                    // Otherwise, use the original calculation
                    startX + (index + 0.5f) * (barWidth + paddingBetweenBars)
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${getDate(timestamp = date).get("day")}.${getDate(timestamp = date).get("month")}.",
                        labelX,
                        size.height - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Draw y-axis labels (categories)
            categories.forEachIndexed { index, category ->
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        category,
                        yAxisX - 10f,
                        yAxisLabelPositions[index],
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PPDBarChart(dates: List<Timestamp>, results: List<String>, categories: List<String>, barColor: Color) {
    val chartHeight = 160f
    val maxBarWidth = 60f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val xAxisY = size.height - 40f
            val yAxisX = 120f
            val chartWidth = size.width - yAxisX - 20f
            val yAxisTop = 30f  // Space for the top label

            val barCount = dates.size
            val totalPaddingWidth = chartWidth * 0.2f
            val paddingBetweenBars = totalPaddingWidth / (barCount + 1)

            // Calculate bar width with a maximum limit
            val calculatedBarWidth = (chartWidth - totalPaddingWidth) / barCount
            val barWidth = minOf(calculatedBarWidth, maxBarWidth)

            // Recalculate total width of all bars
            val totalBarWidth = barWidth * barCount

            // Calculate the starting X position to center the bars
            val startX = yAxisX + (chartWidth - totalBarWidth - (barCount - 1) * paddingBetweenBars) / 2

            // Draw x-axis
            drawLine(
                Color.Black,
                Offset(yAxisX, xAxisY),
                Offset(size.width - 20f, xAxisY),
                strokeWidth = 2f
            )

            // Draw y-axis
            drawLine(
                Color.Black,
                Offset(yAxisX, xAxisY),
                Offset(yAxisX, yAxisTop),
                strokeWidth = 2f
            )

            // Calculate y-axis label positions
            val yAxisLabelPositions = List(categories.size) { index ->
                xAxisY - (index + 1).toFloat() / (categories.size + 1) * (xAxisY - yAxisTop)
            }
            val xAxisCenter = yAxisX + chartWidth / 2

            // Draw bars
            results.forEachIndexed { index, result ->
                val barHeight = when (result) {
                    categories[0] -> yAxisLabelPositions[0] - xAxisY
                    categories[1] -> yAxisLabelPositions[1] - xAxisY
                    categories[2] -> yAxisLabelPositions[2] - xAxisY
                    else -> 0f
                }
                val barX = if (results.size == 1) {
                    // If there's only one bar, center it on the x-axis
                    xAxisCenter - barWidth / 2
                } else {
                    // Otherwise, use the adjusted calculation from before
                    startX + (index + 0.5f) * (barWidth + paddingBetweenBars) - barWidth / 2
                }
                drawRect(
                    barColor,
                    topLeft = Offset(barX, xAxisY),
                    size = Size(barWidth, barHeight),
                )
            }

// Draw x-axis labels (dates)
            dates.forEachIndexed { index, date ->
                val labelX = if (dates.size == 1) {
                    // If there's only one date, center it on the x-axis
                    xAxisCenter
                } else {
                    // Otherwise, use the original calculation
                    startX + (index + 0.5f) * (barWidth + paddingBetweenBars)
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${getDate(timestamp = date).get("day")}.${getDate(timestamp = date).get("month")}.",
                        labelX,
                        size.height - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Draw y-axis labels (categories)
            categories.forEachIndexed { index, category ->
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        category,
                        yAxisX - 10f,
                        yAxisLabelPositions[index],
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }
        }
    }
}

