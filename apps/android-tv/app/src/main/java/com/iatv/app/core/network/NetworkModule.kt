package com.iatv.app.core.network
import com.iatv.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun api(): TvApi = Retrofit.Builder().baseUrl(BuildConfig.API_URL).addConverterFactory(GsonConverterFactory.create()).build().create(TvApi::class.java)
}
