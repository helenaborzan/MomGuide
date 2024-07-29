package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.growthAndDevelopment.Percentile
import hr.ferit.helenaborzan.pregnancyhelper.model.data.common.Point
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun GrowthPercentileChart(
    data: List<Percentile>,
    points: List<Point>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.background(color = LightestPink, shape = RoundedCornerShape(4.dp))) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 60f
        val axisPadding = 20f
        val labelPadding = 40f

        val horizontalLines = 4
        val verticalLines = 12
        val horizontalStep = (canvasHeight - 2 * padding) / horizontalLines
        val verticalStep = (canvasWidth - 2 * padding) / verticalLines

        // Define chart area
        val chartWidth = canvasWidth - 2 * padding
        val chartHeight = canvasHeight - 2 * padding

        // Calculate scales
        val minXValue = data.minOf { it.value }
        val maxXValue = data.maxOf { it.value }
        val maxYValue = data.maxOf { maxOf(it.p3, it.p50, it.p97) }
        val xScale = chartWidth / (maxXValue - minXValue)
        val yScale = chartHeight / maxYValue

        // Draw grid lines
        for (i in 0..horizontalLines) {
            val y = padding + i * horizontalStep
            drawLine(Color.LightGray, Offset(padding, y), Offset(canvasWidth - padding, y))
        }

        for (i in 0..verticalLines) {
            val x = padding + i * verticalStep
            drawLine(Color.LightGray, Offset(x, padding), Offset(x, canvasHeight - padding))
        }

        // Draw axes
        drawLine(Color.Black, Offset(padding, canvasHeight - padding), Offset(canvasWidth - padding, canvasHeight - padding))
        drawLine(Color.Black, Offset(padding, canvasHeight - padding), Offset(padding, padding))

        // Draw percentile lines
        drawPercentileLine(data, xScale, yScale, padding, canvasHeight, Color.Gray, { it.p3 }, "3", minXValue)
        drawPercentileLine(data, xScale, yScale, padding, canvasHeight, Color.Gray, { it.p50 }, "50", minXValue)
        drawPercentileLine(data, xScale, yScale, padding, canvasHeight, Color.Gray, { it.p97 }, "97", minXValue)

        // Draw labels
        drawLabels(minXValue.toInt(), maxXValue.toInt(), maxYValue, padding, canvasWidth, canvasHeight)

        for(point in points){
            drawIndividualDataPoint(point, xScale, yScale, padding, canvasHeight, minXValue)
        }
    }
}

private fun DrawScope.drawPercentileLine(
    data: List<Percentile>,
    xScale: Float,
    yScale: Float,
    padding: Float,
    canvasHeight: Float,
    color: Color,
    heightSelector: (Percentile) -> Float,
    label: String,
    minXValue: Int
) {
    val path = Path()
    var lastPoint: Offset? = null

    data.forEachIndexed { index, point ->
        val x = padding + (point.value - minXValue) * xScale
        val y = canvasHeight - padding - heightSelector(point) * yScale
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        lastPoint = Offset(x, y)
    }
    drawPath(path, color, style = Stroke(width = 2.dp.toPx()))

    // Draw label at the end of the line
    lastPoint?.let {
        drawContext.canvas.nativeCanvas.drawText(
            label,
            it.x + 10f,  // Adjusted to place the label slightly to the right of the point
            it.y,
            android.graphics.Paint().apply {
                textSize = 15f
                textAlign = android.graphics.Paint.Align.LEFT
            }
        )
    }
}



private fun DrawScope.drawLabels(
    minXValue: Int,
    maxXValue: Int,
    maxYValue: Float,
    padding: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val paint = Paint().asFrameworkPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 30f
    }

    // X-axis labels
    val xStep = (maxXValue - minXValue) / 5
    for (x in minXValue..maxXValue step xStep) {
        val xPos = padding + (x - minXValue) * (canvasWidth - 2 * padding) / (maxXValue - minXValue)
        drawContext.canvas.nativeCanvas.drawText(
            x.toString(),
            xPos,
            canvasHeight - padding + 40f,
            paint
        )
    }

    // Y-axis labels
    val yStep = maxYValue / 5
    for (y in 0..maxYValue.toInt() step yStep.toInt()) {
        val yPos = canvasHeight - padding - y * (canvasHeight - 2 * padding) / maxYValue
        drawContext.canvas.nativeCanvas.drawText(
            y.toString(),
            padding - 50f,
            yPos,
            paint
        )
    }

    // X-axis label

}


private fun DrawScope.drawIndividualDataPoint(
    point: Point,
    xScale: Float,
    yScale: Float,
    padding: Float,
    canvasHeight: Float,
    minXValue: Int
) {
    val x = padding + (point.x - minXValue) * xScale
    val y = canvasHeight - padding - point.y * yScale

    drawCircle(
        color = Color.Magenta,
        radius = 5.dp.toPx(),
        center = Offset(x, y)
    )

    val paint = Paint().asFrameworkPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 30f
    }
    drawContext.canvas.nativeCanvas.drawText(
        "${point.x}, ${point.y}",
        x + 10f,
        y - 10f,
        paint
    )
}


