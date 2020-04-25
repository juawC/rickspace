package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.model.Character

class CharacterBoundaryCallback(
    private val characterRepository: CharacterRepository,
    private val singleResourceLoader: SingleResourceLoader = SingleResourceLoader()
) : PagedList.BoundaryCallback<Character>() {

    val networkState: LiveData<Resource<Unit>>
        get() = singleResourceLoader.currentState

    val retryAction: () -> Unit = { singleResourceLoader.retryLastAction() }

    override fun onZeroItemsLoaded() {

        singleResourceLoader.loadData(characterRepository::loadCharactersFirstPage)
    }


    override fun onItemAtEndLoaded(itemAtEnd: Character) {

        singleResourceLoader.loadData { characterRepository.loadCharactersNextPage(itemAtEnd) }
    }

}