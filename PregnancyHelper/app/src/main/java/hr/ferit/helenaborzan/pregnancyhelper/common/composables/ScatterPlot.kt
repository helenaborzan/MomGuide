package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class TimePoint(val hours: Int, val minutes: Int) {
    fun toFloat(): Float = hours + minutes / 60f
}

@Composable
fun ScatterPlot(
    times: List<TimePoint>,
    yValues: List<Float>,
    modifier: Modifier = Modifier,
    pointColor: Color = Color.Blue,
    axisColor: Color = Color.Black
) {
    require(times.size == yValues.size) { "Liste times i yValues moraju biti iste veličine" }

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(300.dp)
    ) {
        val yMin = yValues.minOrNull() ?: 0f
        val yMax = yValues.maxOrNull() ?: 1f

        drawAxis(times, yMin, yMax, axisColor)

        times.zip(yValues).forEach { (time, value) ->
            val xPos = time.toFloat() / 24f * size.width
            val yPos = size.height - ((value - yMin) / (yMax - yMin) * size.height)
            drawCircle(
                color = pointColor,
                radius = 5f,
                center = Offset(xPos, yPos)
            )
        }
    }
}

private fun DrawScope.drawAxis(times: List<TimePoint>, yMin: Float, yMax: Float, color: Color) {
    // X-osa (sati)
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height)
    )

    // Y-osa (vrednosti)
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height)
    )

    // Označavanje x-ose (sati i specifična vremena)
    val hourLabels = listOf(0, 6, 12, 18, 24)
    hourLabels.forEach { hour ->
        val x = size.width * hour / 24f
        drawLine(color, Offset(x, size.height - 5), Offset(x, size.height + 5))
        drawContext.canvas.nativeCanvas.drawText(
            "${hour}:00",
            x,
            size.height + 15,
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 8.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // Označavanje specifičnih vremena
    times.forEach { time ->
        val x = size.width * time.toFloat() / 24f
        drawLine(color, Offset(x, size.height - 3), Offset(x, size.height + 3))
        drawContext.canvas.nativeCanvas.drawText(
            "${time.hours}:${time.minutes.toString().padStart(2, '0')}",
            x,
            size.height + 25,
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 6.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // Označavanje y-ose (vrednosti)
    val yStep = (yMax - yMin) / 5
    for (i in 0..5) {
        val y = size.height - (size.height * i / 5)
        val value = yMin + yStep * i
        drawLine(color, Offset(-5f, y), Offset(5f, y))
        drawContext.canvas.nativeCanvas.drawText(
            String.format("%.1f", value),
            10f,
            y,
            android.graphics.Paint().apply {
                this.color = color.toArgb()
                textSize = 8.sp.toPx()
                textAlign = android.graphics.Paint.Align.LEFT
            }
        )
    }
}



@Preview
@Composable
fun ShowScatterPlot() {
    val x = listOf<TimePoint>(TimePoint(8,1), TimePoint(2,1), TimePoint(14,28), TimePoint(21,2))
    val y = listOf<Float>(2.3F, 1.2F, 4.2F, 9.2F)
    ScatterPlot(times = x, yValues = y)
}