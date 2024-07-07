package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.repository.LMSTableRepository

@Module
@InstallIn(SingletonComponent::class)
object LMSTableRepositoryModule{
    @Provides
    fun provideLMSTableRepository(@ApplicationContext context : Context): LMSTableRepository {
        return LMSTableRepository(context)
    }
}