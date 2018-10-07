package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.di.annotation.ApiConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * App module
 */
@Module
class ApiModule {

    @Provides
    @Singleton
    internal fun provideApi(
            @ApiConfig url: String,
            okHttpClient: OkHttpClient): RickAndMortyService {

        val retrofit = Retrofit.Builder()
                .callFactory(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        return retrofit.create(RickAndMortyService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder().addInterceptor(logging).build()
    }
}