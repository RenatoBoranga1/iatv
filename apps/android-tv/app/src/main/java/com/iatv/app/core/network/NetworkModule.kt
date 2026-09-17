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
    @Provides @Singleton fun api(): TvApi {
        val client = okhttp3.OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .addInterceptor { chain -> chain.proceed(chain.request().newBuilder().header("X-Request-Id", java.util.UUID.randomUUID().toString()).build()) }
            .build()
        return Retrofit.Builder().baseUrl(BuildConfig.API_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(TvApi::class.java)
    }
}
