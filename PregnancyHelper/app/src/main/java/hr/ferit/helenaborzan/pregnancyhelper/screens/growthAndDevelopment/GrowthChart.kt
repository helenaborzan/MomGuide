package hr.ferit.helenaborzan.pregnancyhelper.screens.growthAndDevelopment


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState.Saver.restore
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun GrowthPercentileChart(
    data: List<Percentile>,
    points: List<Point>,
    modifier: Modifier = Modifier,
    xlabel: String,
    ylabel: String
) {
    Canvas(modifier = modifier.background(color = Color.White, shape = RoundedCornerShape(4.dp))) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 100f
        val innerPadding = 50f

        val leftPadding = 150f  // Increased left padding
        val rightPadding = 100f
        val topPadding = 100f
        val bottomPadding = 100f

        val horizontalLines = 4
        val verticalLines = 12
        val horizontalStep = (canvasHeight - topPadding - bottomPadding) / horizontalLines
        val verticalStep = (canvasWidth - leftPadding - rightPadding) / verticalLines

        // Define chart area
        val chartWidth = canvasWidth - innerPadding - leftPadding - rightPadding
        val chartHeight = canvasHeight - innerPadding - topPadding - bottomPadding

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


        drawLabels(minXValue.toInt(), maxXValue.toInt(), maxYValue, padding, canvasWidth, canvasHeight, xlabel, ylabel)

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
            it.x + 10f,
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
    canvasHeight: Float,
    xlabel : String,
    ylabel : String
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

    // Add x-axis label
    drawContext.canvas.nativeCanvas.drawText(
        xlabel,
        canvasWidth / 2,
        canvasHeight - padding + 80f,
        android.graphics.Paint().apply {
            this.color = DarkGray.toArgb()
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
    )

    // Add y-axis label
    drawContext.canvas.nativeCanvas.apply {
        save()
        rotate(-90f, padding - 70f, canvasHeight / 2)
        drawText(
            ylabel,
            padding - 90f,
            canvasHeight / 2,
            android.graphics.Paint().apply {
                this.color = DarkGray.toArgb()
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
        restore()
    }
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
        color = Pink,
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


