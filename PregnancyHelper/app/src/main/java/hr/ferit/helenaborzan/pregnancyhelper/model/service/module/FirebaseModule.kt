package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    fun auth() : FirebaseAuth = Firebase.auth

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder().build()

        val firestore = FirebaseFirestore.getInstance().apply {
            firestoreSettings = settings
        }
        return firestore
    }
}