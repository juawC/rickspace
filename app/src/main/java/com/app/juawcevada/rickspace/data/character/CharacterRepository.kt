package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import timber.log.Timber
import java.lang.Exception

class CharacterRepository(
        private val appDatabase: AppDatabase,
        private val apiService: RickAndMortyService,
        private val itemsByPage: Int
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

    fun loadCharactersNextPage(job: Job, lastCharacter: Character): LiveData<Resource<Unit>> {
        val networkState = MutableLiveData<Resource<Unit>>()
        if (!lastCharacter.hasNextPage()) {
            networkState.postValue(ResourceSuccess())
        } else {
            networkState.postValue(ResourceLoading())

            launch(CommonPool + job) {
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

    fun loadCharactersFirstPage(job: Job): LiveData<Resource<Unit>> {
        val networkState = MutableLiveData<Resource<Unit>>()
        networkState.postValue(ResourceLoading())

        launch(CommonPool + job) {
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

    fun refreshCharactersData(job: Job): LiveData<Resource<Unit>> {
        Timber.d("Refreshing characters...")
        val networkState = MutableLiveData<Resource<Unit>>()
        networkState.postValue(ResourceLoading())

        launch(CommonPool + job) {
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

    fun getCharactersData(job: Job): Listing<Character> {
        Timber.d("Loading characters...")

        val boundaryCallback = CharacterBoundaryCallback(job, this)
        val dataSourceFactory = appDatabase.characterDao().getAllCharacters()
        val pagingBuilder =
                LivePagedListBuilder(dataSourceFactory, itemsByPage)
                        .setBoundaryCallback(boundaryCallback)

        return Listing(pagingBuilder.build(), boundaryCallback.networkState)
    }

    fun getCharacterData(id: Long): LiveData<Character> =
            appDatabase.characterDao().getCharacterById(id)
}