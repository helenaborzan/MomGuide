package hr.ferit.helenaborzan.pregnancyhelper.screens.breastfeeding


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun BreastfeedingPreviewScreen() {

}

@Preview
@Composable
fun BreastfeedingCard() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.breastfeeding_24dp_5f6368_fill0_wght400_grad0_opsz24),
            contentDescription = null
        )
        Text(
            text = "8.01 - 8.25, 24 min, lijeva dojka",
            style = TextStyle(color = Color.Black)
        )
    }
}