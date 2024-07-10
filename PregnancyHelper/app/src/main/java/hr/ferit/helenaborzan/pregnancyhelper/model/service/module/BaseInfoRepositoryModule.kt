package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import hr.ferit.helenaborzan.pregnancyhelper.repository.BaseInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.NewbornInfoRepository
import hr.ferit.helenaborzan.pregnancyhelper.repository.PregnancyInfoRepository
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object BaseInfoRepositoryModule {

    @Provides
    @Named("newborn")
    fun provideNewbornInfoRepository(
        accountService: AccountService,
        firestore: FirebaseFirestore
    ): BaseInfoRepository {
        return NewbornInfoRepository(accountService, firestore)
    }

    @Provides
    @Named("pregnancy")
    fun providePregnancyInfoRepository(
        accountService: AccountService,
        firestore: FirebaseFirestore
    ): BaseInfoRepository {
        return PregnancyInfoRepository(accountService, firestore)
    }
}