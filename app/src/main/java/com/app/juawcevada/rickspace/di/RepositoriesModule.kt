package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.di.annotation.DBConfig
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Singleton
    @Provides
    internal fun provideCharacterRepository(
            appDatabase: AppDatabase,
            apiService: RickAndMortyService,
            @DBConfig itemsByPage: Int,
            appDispatchers: AppDispatchers
    ) = CharacterRepository(appDatabase, apiService, itemsByPage, appDispatchers)
}