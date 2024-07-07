package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun AnswerRadioButton(isSelected : Boolean, onCheckedChange : (Boolean) -> Unit) {
    RadioButton(
        selected = isSelected,
        onClick = { onCheckedChange(!isSelected) },
        colors = RadioButtonDefaults.colors(
            selectedColor = Pink,
            unselectedColor = Pink
        )
    )
}