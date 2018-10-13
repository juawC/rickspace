package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.experimental.Job

class CharacterBoundaryCallback(
        private val job: Job,
        private val characterRepository: CharacterRepository
) : PagedList.BoundaryCallback<Character>() {

    private val singleResourceLoader = SingleResourceLoader()

    val networkState: LiveData<Resource<Unit>>
        get() = singleResourceLoader.currentState

    val retryAction : () -> Unit = {singleResourceLoader.retryLastAction()}

    override fun onZeroItemsLoaded() {

        singleResourceLoader.loadData { characterRepository.loadCharactersFirstPage(job) }
    }


    override fun onItemAtEndLoaded(itemAtEnd: Character) {

        singleResourceLoader.loadData { characterRepository.loadCharactersNextPage(job, itemAtEnd) }
    }

}