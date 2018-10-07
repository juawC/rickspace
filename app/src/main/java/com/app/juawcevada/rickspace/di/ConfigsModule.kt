package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.di.annotation.ApiConfig
import com.app.juawcevada.rickspace.di.annotation.DBConfig
import dagger.Module
import dagger.Provides


@Module
class ConfigsModule {

    @Provides
    @ApiConfig
    fun providesApiUrl(): String {
        return "https://rickandmortyapi.com/api/"
    }

    @Provides
    @DBConfig
    fun providesDbPages(): Int {
        return 20
    }
}