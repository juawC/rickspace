package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.*
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import timber.log.Timber

class CharacterRepository(
        private val appDatabase: AppDatabase,
        private val apiService: RickAndMortyService,
        private val itemsByPage: Int,
        private val appDispatchers: AppDispatchers
) {

    private fun insertResultDb(response: CharacterListInfo) {

        response.results.let { characters ->
            appDatabase.runInTransaction {
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
            coroutineScope: CoroutineScope,
            lastCharacter: Character
    ): LiveData<Resource<Unit>> {
        val networkState = MutableLiveData<Resource<Unit>>()
        if (!lastCharacter.hasNextPage()) {
            networkState.postValue(ResourceSuccess())
        } else {
            networkState.postValue(ResourceLoading())

            coroutineScope.launch(appDispatchers.IO) {
                val result: Result<CharacterListInfo> =
                        apiService.getCharactersByPage(lastCharacter.nextPage).awaitResult().apply {
                            doOnSuccess {
                                try {
                                    appDatabase.runInTransaction {
                                        insertResultDb(it)
                                        networkState.postValue(ResourceSuccess())
                                    }
                                } catch (exception: Exception) {
                                    networkState.postValue(ResourceError())
                                    Timber.e(exception)
                                }
                            }
                        }
                networkState.postValue(result.toResource().map { Unit })
            }
        }
        return networkState
    }

    fun loadCharactersFirstPage(coroutineScope: CoroutineScope): LiveData<Resource<Unit>> {
        val networkState = MutableLiveData<Resource<Unit>>()
        networkState.postValue(ResourceLoading())

        coroutineScope.launch(appDispatchers.IO) {
            val result: Result<CharacterListInfo> = apiService.getCharacters().awaitResult().apply {
                doOnSuccess {
                    insertResultDb(it)
                    networkState.postValue(ResourceSuccess())
                }
            }
            networkState.postValue(result.toResource().map { Unit })
        }
        return networkState
    }

    fun refreshCharactersData(coroutineScope: CoroutineScope): LiveData<Resource<Unit>> {
        Timber.d("Refreshing characters...")
        val networkState = MutableLiveData<Resource<Unit>>()
        networkState.postValue(ResourceLoading())

        coroutineScope.launch(appDispatchers.IO) {
            val result: Result<CharacterListInfo> =
                    apiService.getCharacters().awaitResult().apply {
                        doOnSuccess {
                            appDatabase.runInTransaction {
                                appDatabase.characterDao().deleteAllCharacters()
                                insertResultDb(it)
                                networkState.postValue(ResourceSuccess())
                            }
                        }
                    }
            networkState.postValue(result.toResource().map { Unit })
        }
        return networkState
    }

    fun getCharactersData(coroutineScope: CoroutineScope): Listing<Character> {
        Timber.d("Loading characters...")

        val pagingConfig =
                PagedList
                        .Config.Builder()
                        .setPageSize(itemsByPage)
                        .setPrefetchDistance(itemsByPage)
                        .setEnablePlaceholders(true)
                        .build()
        val boundaryCallback = CharacterBoundaryCallback(coroutineScope, this)
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