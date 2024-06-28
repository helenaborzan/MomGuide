package hr.ferit.helenaborzan.pregnancyhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.helenaborzan.pregnancyhelper.model.Question
import hr.ferit.helenaborzan.pregnancyhelper.navigation.NavigationController
import hr.ferit.helenaborzan.pregnancyhelper.screens.home.LoginAndRegistrationScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.login.LoginScreen
import hr.ferit.helenaborzan.pregnancyhelper.screens.questionnaire.QuestionnaireScreen
import hr.ferit.helenaborzan.pregnancyhelper.ui.theme.PregnancyHelperTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            PregnancyHelperTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationController()
                }
            }
        }
    }
}
