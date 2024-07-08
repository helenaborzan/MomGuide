package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.common.utils.ResourceHelper
import hr.ferit.helenaborzan.pregnancyhelper.repository.LMSTableRepository


@Module
@InstallIn(SingletonComponent::class)
object ResourceHelperModule {
    @Provides
    fun provideResourceHelper(@ApplicationContext context : Context): ResourceHelper {
        return ResourceHelper(context)
    }
}