package com.app.juawcevada.rickspace.ui.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.domain.shared.runInScope
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.ScopedViewModel
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.app.juawcevada.rickspace.ui.shared.ViewStateLiveData
import javax.inject.Inject

@OpenClassOnDebug class CharacterListViewModel @Inject constructor(
        private val getCharactersUseCase: GetCharactersUseCase,
        private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ScopedViewModel(), CharacterListViewActions {


    private val characterListing = runInScope { getCharactersUseCase(Unit) }

    private val characterList: LiveData<PagedList<Character>> = characterListing.pagedList
    private val networkState: LiveData<Resource<Unit>> = characterListing.networkState
    private val refreshNetworkState = MediatorLiveData<Resource<Unit>>()

    private val _viewState = ViewStateLiveData(CharacterListViewState())
    val viewState: LiveData<CharacterListViewState>
        get() = _viewState

    private val _errorMessage = MediatorLiveData<Event<SnackbarMessage>>()
    val errorMessage: LiveData<Event<SnackbarMessage>>
        get() = _errorMessage

    private val _navigationAction = MediatorLiveData<Event<CharacterListNavigationActions>>()
    val navigationAction: LiveData<Event<CharacterListNavigationActions>>
        get() = _navigationAction


    init {
        _viewState.addNewStateSource(characterList) {
            copy(charactersList = it)
        }

        _viewState.addNewStateSource(networkState) {
            val isLoading = charactersList?.isEmpty() ?: true && it is ResourceLoading
            val errorMessage =
                    if (it is ResourceError && charactersList?.isEmpty() != false) {
                        it.error?.toString() ?: ""
                    } else {
                        null
                    }
            copy(isLoading = isLoading, errorMessage = errorMessage)
        }

        _viewState.addNewStateSource(refreshNetworkState) {
            copy(isRefreshing = it is ResourceLoading)
        }

        _errorMessage.addSource(networkState) { stateResource ->
            if (stateResource is ResourceError) {
                _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
            }
        }

        _errorMessage.addSource(refreshNetworkState) { stateResource ->
            if (stateResource is ResourceError) {
                _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
            }
        }

    }

    override fun openCharacter(id: Long) {
        _navigationAction.postValue(Event(CharacterListNavigationActions.OpenCharacterDetail(id)))
    }

    override fun retry() {
        characterListing.retryAction()
    }

    override fun refresh() {
        if (refreshNetworkState.value is ResourceLoading) {
            return
        } else {
            refreshNetworkState.value = ResourceLoading()
        }

        runInScope { refreshCharactersUseCase(Unit) }.also { refreshLiveData ->
            with(refreshNetworkState) {
                addSource(refreshLiveData) {
                    value = it
                    if (it is ResourceSuccess || it is ResourceError) {
                        removeSource(refreshLiveData)
                    }
                }
            }
        }
    }
}
