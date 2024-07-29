package hr.ferit.helenaborzan.pregnancyhelper.model.service.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.ferit.helenaborzan.pregnancyhelper.model.service.EdamamApi
import hr.ferit.helenaborzan.pregnancyhelper.model.service.NutritionixApi
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier


@Module
@InstallIn(SingletonComponent::class)
object NutritionixNetworkModule {

    private const val NUTRITIONIX_API_BASE_URL = "https://trackapi.nutritionix.com/v2/"
    private const val EDAMAM_API_BASE_URL = "https://api.edamam.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NutritionixRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class EdamamRetrofit
    @Provides
    @Singleton
    @NutritionixRetrofit
    fun providenNutritionixRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NUTRITIONIX_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNutritionixApi(@NutritionixRetrofit retrofit: Retrofit) : NutritionixApi {
        return retrofit.create(NutritionixApi::class.java)
    }

    @Provides
    @Singleton
    @EdamamRetrofit
    fun provideEdamamRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EDAMAM_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideEdamamApi(@EdamamRetrofit retrofit: Retrofit) : EdamamApi {
        return retrofit.create(EdamamApi::class.java)
    }


}