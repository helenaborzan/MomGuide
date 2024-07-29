package hr.ferit.helenaborzan.pregnancyhelper.screens.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe

@Composable
fun RecipeScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val recipes by viewModel.recipes.collectAsState()

    Column {
        var query by remember { mutableStateOf("") }
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Enter ingredient") },
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { viewModel.fetchRecipes(query) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Search")
        }
        LazyColumn {
            items(recipes) { recipe ->
                RecipeItem(recipe)
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = recipe.label)
        Image(
            painter = rememberImagePainter(recipe.image),
            contentDescription = null,
            modifier = Modifier.height(150.dp).fillMaxWidth()
        )
        Button(onClick = { /* Open recipe.url */ }) {
            Text("View Recipe")
        }
    }
}