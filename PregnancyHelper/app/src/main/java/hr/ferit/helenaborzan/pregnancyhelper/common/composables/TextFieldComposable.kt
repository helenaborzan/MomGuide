package hr.ferit.helenaborzan.pregnancyhelper.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import io.data2viz.charts.core.Padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    label: String,
    value : String,
    @DrawableRes icon : Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    onValueChange : (String) -> Unit,
    modifier : Modifier = Modifier
){
    Column (modifier = Modifier.border(width = 0.dp, color = Color.Transparent)){
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = DarkGray, fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = 1,
            modifier = modifier
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(4.dp))
                    .background(color = Color.White)
                    .height(36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.padding(4.dp),
                    tint = DarkGray
                )
                it.invoke()
            }
        }
    }
}

@Composable
fun PasswordTextField(
    label: String,
    value : String,
    @DrawableRes icon : Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    onValueChange : (String) -> Unit,
    modifier : Modifier = Modifier
){
    var isPasswordVisible by remember { mutableStateOf(false) }
    var visibilityIcon = if (isPasswordVisible)
            R.drawable.baseline_visibility_off_24
        else
            R.drawable.baseline_visibility_24
    Column (modifier = Modifier.border(width = 0.dp, color = Color.Transparent)){
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = DarkGray, fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = 1,
            modifier = modifier,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(4.dp))
                    .background(color = Color.White)
                    .height(36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.padding(4.dp),
                    tint = DarkGray
                )
                it.invoke()
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    Icon(
                        painter = if (isPasswordVisible) painterResource(R.drawable.baseline_visibility_off_24)
                        else painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                isPasswordVisible = !isPasswordVisible
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LabeledTextField(
    labelId: Int,
    unitId : Int,
    value : String,
    onValueChange : (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    fontSize : TextUnit = 16.sp,
    padding: PaddingValues = PaddingValues(16.dp),
    modifier: Modifier =  Modifier
        .border(width = 1.dp, color = Pink, shape = RoundedCornerShape(2.dp))
        .background(color = Color.White)
) {
    Column(modifier = Modifier
        .border(width = 0.dp, color = Color.Transparent)
        .padding(paddingValues = padding)
        ) {
        Text(
            text = stringResource(id = labelId),
            style = TextStyle(color = DarkGray, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = DarkGray, fontSize = fontSize),
                keyboardOptions = keyboardOptions,
                modifier = modifier.padding(horizontal = 8.dp)
            ) {
                it.invoke()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(id = unitId),
                style = TextStyle(color = Color.Black, fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}

