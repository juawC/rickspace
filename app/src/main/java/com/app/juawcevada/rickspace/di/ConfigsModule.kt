package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.di.annotation.ApiConfig
import dagger.Module
import dagger.Provides


@Module
class ConfigsModule {

    @Provides
    @ApiConfig
    fun providesApiUrl(): String {
        return "https://rickandmortyapi.com/api/"
    }
}