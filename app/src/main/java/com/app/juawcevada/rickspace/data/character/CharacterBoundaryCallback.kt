package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.experimental.Job

class CharacterBoundaryCallback(
        private val job: Job,
        private val characterRepository: CharacterRepository)
    : PagedList.BoundaryCallback<Character>() {

    private val _networkState: MediatorLiveData<Resource<Unit>> = MediatorLiveData()
    val networkState: LiveData<Resource<Unit>>
        get() = _networkState

    override fun onZeroItemsLoaded() {
        // Avoid concurrent network calls
        if (networkState.value is ResourceLoading) return

        characterRepository.loadCharactersFirstPage(job).let { responseState ->
            _networkState.addSource(responseState) {
                _networkState.postValue(it)

                if (it is ResourceSuccess || it is ResourceError) {
                    _networkState.removeSource(responseState)
                }
            }
        }
    }


    override fun onItemAtEndLoaded(itemAtEnd: Character) {
        // Avoid concurrent network calls
        if (networkState.value is ResourceLoading) return

        characterRepository.loadCharactersNextPage(job, itemAtEnd).let { responseState ->
            _networkState.addSource(responseState) {
                _networkState.postValue(it)

                if (it is ResourceSuccess || it is ResourceError) {
                    _networkState.removeSource(responseState)
                }
            }
        }
    }
}