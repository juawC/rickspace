package com.app.juawcevada.rickspace.ui.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.domain.shared.invoke
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import javax.inject.Inject

class CharacterListViewModel @Inject constructor(
        private val getCharactersUseCase: GetCharactersUseCase,
        private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ViewModel(), CharacterListViewActions {

    private val characterListing = getCharactersUseCase()

    private val characterList: LiveData<PagedList<Character>> = characterListing.pagedList
    private val networkState: LiveData<Resource<Unit>> = characterListing.networkState

    private val refreshNetworkState = MediatorLiveData<Resource<Unit>>()

    private val _viewState = MediatorLiveData<CharacterListViewState>().apply {
        value = CharacterListViewState()
    }
    val viewState: LiveData<CharacterListViewState>
        get() = _viewState

    private val _errorMessage = MediatorLiveData<Event<SnackbarMessage>>()
    val errorMessage: LiveData<Event<SnackbarMessage>>
        get() = _errorMessage

    private val _navigationAction = MediatorLiveData<Event<CharacterListNavigationActions>>()
    val navigationAction: LiveData<Event<CharacterListNavigationActions>>
        get() = _navigationAction


    init {
        _viewState.addSource(characterList) { characterList ->
            _viewState.value?.run {
                // TODO fix this
                _viewState.value = copy(charactersList = characterList)
            }
        }

        _viewState.addSource(networkState) { stateResource ->
            _viewState.value?.run {
                val isLoading = charactersList?.isEmpty() ?: true && stateResource is ResourceLoading
                val errorMessage =
                        if (stateResource is ResourceError) {
                            stateResource.error?.toString() ?: ""
                        } else {
                            null
                        }
                _viewState.value = copy(isLoading = isLoading, errorMessage = errorMessage)
            }
        }

        _viewState.addSource(refreshNetworkState) { refreshResource ->
            _viewState.value?.run {
                _viewState.value = copy(isRefreshing = refreshResource is ResourceLoading)
            }
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
        if (refreshNetworkState.value is ResourceLoading) return

        refreshCharactersUseCase().also { refreshLiveData ->
            with(refreshNetworkState) {
                addSource(refreshLiveData) {
                    postValue(it)
                    if (it is ResourceSuccess || it is ResourceError) {
                        removeSource(refreshLiveData)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getCharactersUseCase.cancel()
        refreshCharactersUseCase.cancel()
    }
}
