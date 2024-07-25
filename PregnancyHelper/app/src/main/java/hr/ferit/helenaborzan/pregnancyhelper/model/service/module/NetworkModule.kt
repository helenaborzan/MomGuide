package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.model.service.NutritionixApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.nutritionix.com/"

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNutritionixApi(retrofit: Retrofit) : NutritionixApi{
        return retrofit.create(NutritionixApi::class.java)
    }
}