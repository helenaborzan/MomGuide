package hr.ferit.helenaborzan.pregnancyhelper.screens.recipe

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue

@Composable
fun RecipeScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val recipes by viewModel.recipes.collectAsState()

    Column {
        var query by rememberSaveable { mutableStateOf("") }
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Enter ingredient") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = { viewModel.fetchRecipes(query) },
                enabled = query.isNotBlank(),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Search")
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            content = {
                items(recipes.size) { index ->
                    RecipeItem(recipes[index])
                }
            }
        )
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(0.7f)
            .background(color = LightBlue),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = recipe.label,
                    style = TextStyle(color = DarkGray, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 40.dp) // Dodajemo padding s desne strane
                        .fillMaxWidth()
                )
                IconButton(
                    onClick = { /* Dodajte akciju za favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (false) Color.Red else Color.Gray
                    )
                }
            }
            Image(
                painter = rememberImagePainter(recipe.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("View Recipe")
            }
        }
    }
}