package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationContextModule {    @Provides
    fun provideContext(@ApplicationContext context: Context?): Context? {
        return context
    }
}