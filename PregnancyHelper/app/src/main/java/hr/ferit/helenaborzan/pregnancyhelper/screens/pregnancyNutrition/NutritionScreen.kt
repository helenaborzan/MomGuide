package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.model.data.nutritionix.Food
import hr.ferit.helenaborzan.pregnancyhelper.navigation.Screen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.Pink

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel(),
    navController : NavController
) {
    val foodDataState by viewModel.foodData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val foodDetails by viewModel.foodDetails.collectAsState()
    Log.d("NutritionScreen", "Food details size: ${foodDetails.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var query by remember { mutableStateOf("") }
        GoBackIconBar(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            navController = navController
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Food") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.searchFood(query)
            },
            enabled = query.isNotBlank()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            foodDetails.isNotEmpty() -> {
                LazyColumn {
                    items(foodDetails) { item ->
                        if (item != null) {
                            FoodItem(
                                item = item,
                                viewModel = viewModel,
                                navController = navController
                            )
                        } else {
                            Text("Null item", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
            foodDataState?.isFailure == true -> {
                val error = foodDataState?.exceptionOrNull()
                Text("Error: ${error?.message}", color = Color.Red)
            }
            else -> {
                Column (modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("No results found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun FoodItem(
    item: Food,
    viewModel: NutritionViewModel,
    navController: NavController
){
    var showMore by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodImage(item = item, modifier = Modifier.weight(0.15f))
                FoodNutrientsValues(item = item, showMore = showMore, modifier = Modifier.weight(0.6f))
                AddFoodAndMore(
                    modifier = Modifier.weight(0.25f),
                    onButtonClick = {
                        viewModel.addUsersFoodIntake(food = item)
                                    navController.navigate(Screen.PregnancyHomeScreen.route)},
                    onMoreIconClick = { showMore = ! showMore },
                    showMore = showMore
                )
            }
        }
    }
}

@Composable
fun FoodNutrientsValues(
    modifier : Modifier = Modifier,
    item : Food,
    showMore: Boolean
) {
    Column (modifier = modifier){
        Text(
            text = item.food_name ?: "",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        item.brand_name?.let { brand ->
            Text(
                text = "Brand: $brand",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item.nf_calories?.let { calories ->
            Text("Calories: $calories", style = MaterialTheme.typography.bodySmall)
        }
        if (showMore) {
            item.nf_total_fat?.let { fat ->
                Text("Total Fat: ${fat}g", style = MaterialTheme.typography.bodySmall)
            }
            item.nf_total_carbohydrate?.let { carbs ->
                Text("Total Carbohydrates: ${carbs}g", style = MaterialTheme.typography.bodySmall)
            }
            item.nf_protein?.let { protein ->
                Text("Protein: ${protein}g", style = MaterialTheme.typography.bodySmall)
            }
            item.nf_dietary_fiber?.let { fiber ->
                Text("Dietary Fiber: ${fiber}g", style = MaterialTheme.typography.bodySmall)
            }
            item.nf_sugars?.let { sugars ->
                Text("Sugars: ${sugars}g", style = MaterialTheme.typography.bodySmall)
            }
            item.nf_sodium?.let { sodium ->
                Text("Sodium: ${sodium}mg", style = MaterialTheme.typography.bodySmall)
            }
        }
        item.serving_qty?.let { qty ->
            item.serving_unit?.let { unit ->
                Text(
                    text = "Serving: $qty $unit",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FoodImage(item : Food, modifier: Modifier = Modifier) {

    Box(modifier = modifier.padding(4.dp)) {
        AsyncImage(
            model = item.photo?.thumb,
            contentDescription = "Food thumbnail",
            modifier = modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun AddFoodAndMore(
    modifier: Modifier = Modifier,
    onButtonClick : () -> Unit,
    onMoreIconClick : () -> Unit,
    showMore : Boolean
) {

    Row (modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically){

        Text(
            text = "+",
            style = TextStyle(color = Pink,fontWeight = FontWeight.Bold, fontSize = 24.sp),
            modifier = Modifier.padding(4.dp)
                .clickable { onButtonClick() }
        )
        Icon(
            painter = if (showMore) painterResource(id = R.drawable.baseline_expand_less_24)
                    else painterResource(id = R.drawable.baseline_more_horiz_24),
            contentDescription = stringResource(id = R.string.moreIconDescription),
            tint = DarkGray,
            modifier = Modifier.clickable {
                onMoreIconClick()
            }
        )

    }
}