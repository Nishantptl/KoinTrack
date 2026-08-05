package com.nishant.kointrack.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nishant.kointrack.data.remote.ExchangeRateApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideExchangeRateApi(json: Json): ExchangeRateApi {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ExchangeRateApi::class.java)
    }
}
