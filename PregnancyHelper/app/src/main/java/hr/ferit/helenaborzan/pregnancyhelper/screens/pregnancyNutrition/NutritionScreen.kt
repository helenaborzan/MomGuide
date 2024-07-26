package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import hr.ferit.helenaborzan.pregnancyhelper.R
import hr.ferit.helenaborzan.pregnancyhelper.model.Food
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightPink

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val foodDataState by viewModel.foodData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val foodDetails by viewModel.foodDetails.collectAsState()
    Log.d("NutritionScreen", "Food details size: ${foodDetails.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var query by remember { mutableStateOf("") }

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
                            FoodItem(item)
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
                Text("No results found", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun FoodItem(item: Food) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodImage(item = item)
                Spacer(modifier = Modifier.width(16.dp))
                FoodNutrientsValues(item = item)
                AddFoodAndMore()
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FoodNutrientsValues(item : Food) {
    Column {
        Text(
            text = item.food_name ?: "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        item.brand_name?.let { brand ->
            Text(
                text = "Brand: $brand",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item.nf_calories?.let { calories ->
            Text("Calories: $calories", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_total_fat?.let { fat ->
            Text("Total Fat: ${fat}g", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_total_carbohydrate?.let { carbs ->
            Text("Total Carbohydrates: ${carbs}g", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_protein?.let { protein ->
            Text("Protein: ${protein}g", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_dietary_fiber?.let { fiber ->
            Text("Dietary Fiber: ${fiber}g", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_sugars?.let { sugars ->
            Text("Sugars: ${sugars}g", style = MaterialTheme.typography.bodyMedium)
        }
        item.nf_sodium?.let { sodium ->
            Text("Sodium: ${sodium}mg", style = MaterialTheme.typography.bodyMedium)
        }
        item.serving_qty?.let { qty ->
            item.serving_unit?.let { unit ->
                Text(
                    text = "Serving: $qty $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FoodImage(item : Food) {
    AsyncImage(
        model = item.photo?.thumb,
        contentDescription = "Food thumbnail",
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun AddFoodAndMore() {
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically){
        Button(
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonColors(
                containerColor = LightPink,
                contentColor = DarkGray,
                disabledContainerColor = LightPink,
                disabledContentColor = DarkGray),
            onClick = {  }
        ) {
            Text(text = stringResource(id = R.string.add))
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_more_horiz_24),
            contentDescription = stringResource(id = R.string.moreIconDescription),
            tint = DarkGray,
            modifier = Modifier.clickable {  }
        )

    }
}