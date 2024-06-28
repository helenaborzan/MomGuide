package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository


@Module
@InstallIn(SingletonComponent::class)
object QuestionnaireRepositoryModule {
    @Provides
    fun provideQuestionRepository(firestore: FirebaseFirestore): QuestionnaireRepository {
        return QuestionnaireRepository(firestore)
    }
}