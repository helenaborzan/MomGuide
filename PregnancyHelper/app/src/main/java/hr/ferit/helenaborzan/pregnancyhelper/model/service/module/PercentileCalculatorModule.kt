package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.PercentileCalculator
import hr.ferit.helenaborzan.pregnancyhelper.repository.LMSTableRepository

@Module
@InstallIn(SingletonComponent::class)
object PercentileCalculatorModule {
    @Provides
    fun providePercentileCalculator(LMSTableRepository : LMSTableRepository) : PercentileCalculator{
        return PercentileCalculator(LMSTableRepository)
    }
}