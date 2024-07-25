package hr.ferit.helenaborzan.pregnancyhelper.screens.pregnancyNutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import hr.ferit.helenaborzan.pregnancyhelper.model.Hit

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val foodData by viewModel.foodData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var query by remember { mutableStateOf("apple") }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Food") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.searchFood(query) }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(foodData?.hits ?: emptyList()) { hit ->
                FoodItem(hit)
            }
        }
    }
}

@Composable
fun FoodItem(hit: Hit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Name: ${hit.fields.item_name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Brand: ${hit.fields.brand_name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Calories: ${hit.fields.nf_calories} kcal", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fat: ${hit.fields.nf_total_fat} g", style = MaterialTheme.typography.bodyMedium)
        }
    }
}