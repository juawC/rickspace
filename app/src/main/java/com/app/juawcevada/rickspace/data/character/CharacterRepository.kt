package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import arrow.core.Try
import com.app.juawcevada.rickspace.data.shared.repository.createLiveDataDataSource
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.gildor.coroutines.retrofit.await
import timber.log.Timber

class CharacterRepository(
        private val appDatabase: AppDatabase,
        private val apiService: RickAndMortyService,
        private val appDispatchers: AppDispatchers
) {

    suspend fun refreshCharactersData(): Try<Unit> {
        Timber.d("Refreshing characters...")

        return withContext(appDispatchers.IO) {
            Try { apiService.getCharacters().await() }.map {
                appDatabase.runInTransaction {
                    appDatabase.characterDao().deleteAllCharacters()
                    insertCharactersIntoDb(it)
                }
            }
        }
    }

    fun getCharactersData(coroutineScope: CoroutineScope): LiveData<Resource<List<Character>>> {
        Timber.d("Loading characters...")

        return createLiveDataDataSource(
                coroutineScope,
                appDispatchers,
                apiService::getCharacters,
                appDatabase.characterDao()::getAllCharacters,
                ::insertCharactersIntoDb)

    }

    fun getCharacterData(id: Long): LiveData<Character> =
            appDatabase.characterDao().getCharacterById(id)

    private fun insertCharactersIntoDb(response: CharacterListInfo) {
        appDatabase.characterDao().insertAll(response.results)
    }
}