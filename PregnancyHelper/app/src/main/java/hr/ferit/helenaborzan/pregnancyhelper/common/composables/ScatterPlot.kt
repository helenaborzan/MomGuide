package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.model.data.common.TimePoint
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink


@Composable
fun ScatterPlot(
    times: List<TimePoint>,
    yValues: List<Int>,
    modifier: Modifier = Modifier,
    pointColor: Color = Pink,
    axisColor: Color = Color.Black,
    xlabel: String,
    ylabel: String
) {
    require(times.size == yValues.size) { "Liste times i yValues moraju biti iste veličine" }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color.White)
    ) {
        val yMin = yValues.minOrNull() ?: 0
        val yMax = yValues.maxOrNull() ?: 1

        val yRange = maxOf(yMax - yMin, 10)
        val adjustedYMin = maxOf(0, yMin - (yRange * 0.1f).toInt())
        val adjustedYMax = adjustedYMin + (yRange * 1.2f).toInt()

        val xPadding = 100f
        val yPadding = 100f
        val innerPadding = 50f

        val plotArea = size.copy(
            width = size.width - xPadding - innerPadding * 2,
            height = size.height - yPadding - innerPadding * 2
        )

        drawAxis(
            times, adjustedYMin.toFloat(), adjustedYMax.toFloat(),
            axisColor, plotArea, xPadding, yPadding, innerPadding, xlabel, ylabel
        )

        times.zip(yValues).forEach { (time, value) ->
            val xPos = xPadding + innerPadding + (time.toFloat() / 24f * plotArea.width)
            val yPos = innerPadding + plotArea.height - ((value - adjustedYMin).toFloat() / (adjustedYMax - adjustedYMin) * plotArea.height)
            drawCircle(
                color = pointColor,
                radius = 7f,
                center = Offset(xPos, yPos)
            )
        }
    }
}

private fun DrawScope.drawAxis(
    times: List<TimePoint>,
    yMin: Float,
    yMax: Float,
    color: Color,
    plotArea: Size,
    xPadding: Float,
    yPadding: Float,
    innerPadding: Float,
    xlabel: String,
    ylabel: String
) {
    // X-axis
    drawLine(
        color = color,
        start = Offset(xPadding, innerPadding + plotArea.height),
        end = Offset(size.width - innerPadding, innerPadding + plotArea.height)
    )

    // Y-axis
    drawLine(
        color = color,
        start = Offset(xPadding, innerPadding),
        end = Offset(xPadding, innerPadding + plotArea.height)
    )

    // X-axis labels (every third hour)
    for (hour in 0..24 step 4) {
        val x = xPadding + innerPadding + (plotArea.width * hour / 24f)
        drawLine(color, Offset(x, innerPadding + plotArea.height), Offset(x, innerPadding + plotArea.height + 5))
        drawContext.canvas.nativeCanvas.drawText(
            "${hour}:00",
            x,
            innerPadding + plotArea.height + 40f,  // Adjust this value to change the padding
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // Y-axis labels
    val yStep = (yMax - yMin) / 5
    for (i in 0..5) {
        val y = innerPadding + plotArea.height - (plotArea.height * i / 5)
        val value = yMin + yStep * i
        drawLine(color, Offset(xPadding - 5, y), Offset(xPadding, y))
        drawContext.canvas.nativeCanvas.drawText(
            String.format("%.0f", value),
            xPadding - 15,
            y + 5,
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
            }
        )
    }

    // X-axis label
    drawContext.canvas.nativeCanvas.drawText(
        xlabel,
        xPadding + plotArea.width / 2,
        size.height - 50,
        android.graphics.Paint().apply {
            this.color = color.toArgb()
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
    )

    // Y-axis label
    drawContext.canvas.nativeCanvas.apply {
        save()
        rotate(-90f, xPadding / 4, size.height / 2)
        drawText(
            ylabel,
            xPadding / 4,
            size.height / 2,
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
        restore()
    }
}

@Preview
@Composable
fun ShowScatterPlot() {
    val x = listOf<TimePoint>(TimePoint(8,1), TimePoint(2,1), TimePoint(14,28), TimePoint(21,2))
    val y = listOf<Int>(20, 11, 50, 45)
    ScatterPlot(times = x, yValues = y, xlabel = "xos", ylabel = "yos")
}