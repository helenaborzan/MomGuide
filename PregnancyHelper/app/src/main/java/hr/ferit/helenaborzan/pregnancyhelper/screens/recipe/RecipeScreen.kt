package hr.ferit.helenaborzan.pregnancyhelper.screens.recipe

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import hr.ferit.helenaborzan.pregnancyhelper.common.composables.GoBackIconBar
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.Recipe
import hr.ferit.helenaborzan.pregnancyhelper.model.data.edamam.RecipeInfo
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.DarkGray
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.LightBlue

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    navController: NavController
) {
    val recipes by viewModel.recipes.collectAsState()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column {
        var query by rememberSaveable { mutableStateOf("") }
        GoBackIconBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            navController = navController
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search recipes") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.fetchRecipes(query) },
                enabled = query.isNotBlank() && !isLoading,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Search")
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                content = {
                    items(recipes.size) { index ->
                        RecipeItem(
                            recipe = recipes[index],
                            viewModel = viewModel,
                            isFavourite = viewModel.isFavourite(recipe = recipes[index], favoriteRecipes = favoriteRecipes)
                        )
                    }
                }
            )
        }
    }
}
@Composable
fun RecipeItem(
    recipe: Recipe,
    isFavourite : Boolean,
    viewModel: RecipeViewModel
) {
    val context = LocalContext.current
    val likeCount by viewModel.getLikeCount(recipe.url).collectAsState(initial = 0)
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
                    style = TextStyle(color = DarkGray, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 40.dp)
                        .fillMaxWidth()
                )
                Row (
                    modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "${likeCount}", color = DarkGray)
                    IconButton(
                        onClick = {
                            Log.d("RecipeItem", "Clicked favorite for ${recipe.label}")
                            viewModel.toggleFavorite(
                                RecipeInfo(
                                    label = recipe.label,
                                    image = recipe.image,
                                    url = recipe.url
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavourite) Color.Red else Color.Gray
                        )
                    }
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
                Text("View")
            }
        }
    }
}