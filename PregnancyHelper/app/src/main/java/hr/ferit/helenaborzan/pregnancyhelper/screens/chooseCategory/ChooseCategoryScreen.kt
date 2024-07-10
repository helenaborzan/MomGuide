package hr.ferit.helenaborzan.pregnancyhelper.screens.chooseCategory

import android.graphics.fonts.FontStyle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightestPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun ChooseCategoryScreen(
    navController: NavController,
    viewModel: ChooseCategoryViewModel = hiltViewModel()) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightestPink)
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = stringResource(id = R.string.chooseCategory),
            style = TextStyle(color = DarkGray, fontSize = 28.sp),
            modifier = Modifier.padding(12.dp)
        )
        Column (
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CategoryCard(
                imageResource = R.drawable.pregnancy_clip_art,
                stringResource = R.string.pregnancyCategory,
                borderColor = Pink,
                onClick = {
                    viewModel.onPregnancyCategoryClick()
                    navController.navigate(Screen.PregnancyHomeScreen.route)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            CategoryCard(
                imageResource = R.drawable.newborns_clip_art,
                stringResource = R.string.newbornsCategory,
                borderColor = DarkGray,
                onClick = {
                    viewModel.onNewbornsCategoryClick()
                    navController.navigate(Screen.NewbornHomeScreen.route)
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    @DrawableRes imageResource : Int,
    @StringRes stringResource : Int,
    borderColor : Color,
    onClick : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .height(120.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.TopEnd
    ){
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(id = stringResource),
            style = TextStyle(color = DarkGray, fontSize = 14.sp),
            modifier = Modifier.padding(8.dp)
        )

    }
    
}

