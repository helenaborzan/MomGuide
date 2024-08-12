package hr.ferit.helenaborzan.pregnancyhelper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.helenaborzan.pregnancyhelper.navigation.NavigationController
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.PregnancyHelperTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            PregnancyHelperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Places.initialize(applicationContext, "AIzaSyBxc0Sg4d_F2fxVZJKan4ONFWbNwpKlal4")
                    NavigationController()
                }
            }
        }
    }
}
