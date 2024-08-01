package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.QuestionnaireRepository

@Module
@InstallIn(SingletonComponent::class)
object PregnancyIfoRepositoryModule {
    @Provides
    fun providePregnancyInfoRepository(accountService: AccountService, firestore: FirebaseFirestore, questionnaireRepository: QuestionnaireRepository): PregnancyInfoRepository {
        return PregnancyInfoRepository(accountService = accountService, firestore = firestore, questionnaireRepository)
    }
}