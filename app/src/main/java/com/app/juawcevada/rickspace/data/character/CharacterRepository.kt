package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.extensions.runInTransactionSuspended
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class CharacterRepository(
    private val appDatabase: AppDatabase,
    private val apiService: RickAndMortyService,
    private val itemsByPage: Int,
    private val appDispatchers: AppDispatchers
) {

    private fun insertResultDb(response: CharacterListInfo) {
        response.results.let { characters ->
            appDatabase.runInTransaction() {
                // Update items with a calculated indexInResponse
                val start = appDatabase.characterDao().getNextIndexCharacter()
                val charactersUpdated = characters.mapIndexed { index, character ->
                    character.apply {
                        indexInResponse = start + index
                        nextPage = response.info.nextPageInt ?: -1
                    }
                }
                appDatabase.characterDao().insertAll(charactersUpdated)
            }
        }
    }

    fun loadCharactersNextPage(
        lastCharacter: Character
    ): Flow<Resource<Unit>> = flow {
        if (!lastCharacter.hasNextPage()) {
            emit(ResourceSuccess())
        } else {
            emit(ResourceLoading())
            try {
                val result = apiService.getCharactersByPage(lastCharacter.nextPage)
                appDatabase.runInTransactionSuspended(appDispatchers.IO) {
                    insertResultDb(result)
                }
                emit(ResourceSuccess())
            } catch (exception: Exception) {
                emit(ResourceError(error = exception))
                Timber.e(exception)
            }
        }
    }

    fun loadCharactersFirstPage(): Flow<Resource<Unit>> = flow {
        emit(ResourceLoading())
        try {
            val result = apiService.getCharacters()
            appDatabase.runInTransactionSuspended(appDispatchers.IO) {
                insertResultDb(result)
            }
            emit(ResourceSuccess())
        } catch (exception: Exception) {
            emit(ResourceError(error = exception))
            Timber.e(exception)
        }
    }


    fun refreshCharactersData(): Flow<Resource<Unit>> = flow {
        emit(ResourceLoading())
        try {
            val result = apiService.getCharacters()
            appDatabase.runInTransactionSuspended(appDispatchers.IO) {
                appDatabase.characterDao().deleteAllCharacters()
                insertResultDb(result)
            }
            emit(ResourceSuccess())
        } catch (exception: Exception) {
            emit(ResourceError(error = exception))
            Timber.e(exception)
        }
    }

    fun getCharactersData(): Listing<Character> {
        val pagingConfig =
                PagedList
                        .Config.Builder()
                        .setPageSize(itemsByPage)
                        .setPrefetchDistance(itemsByPage)
                        .setEnablePlaceholders(true)
                        .build()
        val boundaryCallback = CharacterBoundaryCallback(this)
        val dataSourceFactory = appDatabase.characterDao().getAllCharacters()
        val pagingBuilder =
                LivePagedListBuilder(dataSourceFactory, pagingConfig)
                        .setBoundaryCallback(boundaryCallback)

        return Listing(
                pagingBuilder.build(),
                boundaryCallback.networkState,
                boundaryCallback.retryAction)
    }

    fun getCharacterData(id: Long): LiveData<Character> =
            appDatabase.characterDao().getCharacterById(id)
}