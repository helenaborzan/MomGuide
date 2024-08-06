package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink


@Composable
fun BasicButton(
    text : String,
    borderColor : Color = Pink,
    containerColor : Color = Color.White,
    onClick : () -> Unit,
    modifier : Modifier = Modifier.border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(36.dp))
                                .size(width = 200.dp, height = 36.dp)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        modifier = modifier

    ) {
        Text (
            text = text,
            style = TextStyle(color = DarkGray, fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}

@Composable
fun BasicButtonLargerFont(
    text : String,
    borderColor : Color = Pink,
    containerColor : Color = Color.White,
    onClick : () -> Unit,
    modifier : Modifier = Modifier.border(width = 1.dp, color = Pink, shape = RoundedCornerShape(36.dp))
        .size(width = 200.dp, height = 40.dp)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        modifier = modifier

    ) {
        Text (
            text = text,
            style = TextStyle(color = DarkGray, fontSize = 12.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}

@Composable
fun ButtonWithGradient(
    text: String,
    borderColor: Color = Pink,
    colors: List<Color> = listOf(LightPink, Pink),
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Pink,
                shape = RoundedCornerShape(80.dp)
            )
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(80.dp)
            )
            .size(width = 200.dp, height = 36.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
    ) {
        Text (
            text = text,
            style = TextStyle(color = DarkGray, fontSize = 12.sp, fontFamily = FontFamily.SansSerif)
        )
    }
}