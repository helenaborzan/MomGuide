package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import hr.ferit.helenaborzan.pregnancyhelper.model.FoodItem
import hr.ferit.helenaborzan.pregnancyhelper.model.Item
import hr.ferit.helenaborzan.pregnancyhelper.model.NutritionixResponse

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val foodDataState by viewModel.foodData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
            onClick = { viewModel.searchFood(query) },
            enabled = query.isNotBlank()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            foodDataState?.isSuccess == true -> {
                val nutritionixResponse = foodDataState?.getOrNull()
                if (nutritionixResponse != null) {
                    Text("Common results: ${nutritionixResponse.common.size}, Branded results: ${nutritionixResponse.branded.size}")
                    LazyColumn {
                        items(nutritionixResponse.common) { item ->
                            FoodItem(item, isCommon = true)
                        }
                        items(nutritionixResponse.branded) { item ->
                            FoodItem(item, isCommon = false)
                        }
                    }
                } else {
                    Text("No results found", modifier = Modifier.padding(16.dp))
                }
            }
            foodDataState?.isFailure == true -> {
                val error = foodDataState?.exceptionOrNull()
                Text("Error: ${error?.message}", color = Color.Red)
            }
        }
    }
}

@Composable
fun FoodItem(item: FoodItem, isCommon: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.photo?.thumb,
                contentDescription = "Food thumbnail",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.food_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!isCommon) {
                    item.brand_name?.let { brand ->
                        Text(
                            text = "Brand: $brand",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                item.nf_calories?.let { calories ->
                    Text(
                        text = "Calories: $calories",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                item.serving_qty?.let { qty ->
                    item.serving_unit?.let { unit ->
                        Text(
                            text = "Serving: $qty $unit",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}