package com.app.juawcevada.rickspace.ui.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.domain.shared.invoke
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.app.juawcevada.rickspace.ui.shared.ViewStateLiveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenClassOnDebug
class CharacterListViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ViewModel(), CharacterListViewActions {

    private val charactersListing = getCharactersUseCase()

    private val _errorMessage = MediatorLiveData<Event<SnackbarMessage>>()
    val errorMessage: LiveData<Event<SnackbarMessage>>
        get() = _errorMessage

    private val _navigationAction = MediatorLiveData<Event<CharacterListNavigationActions>>()
    val navigationAction: LiveData<Event<CharacterListNavigationActions>>
        get() = _navigationAction

    private val _viewState = ViewStateLiveData(CharacterListViewState()).apply {
        addNewStateSource(charactersListing.pagedList) { newCharactersList ->
            copy(charactersList = newCharactersList)
        }
        addNewStateSource(charactersListing.networkState) { newNetworkState ->
            val isLoading = isScreenLoading(newNetworkState)
            val errorMessage = getErrorMessage(newNetworkState)
            copy(isLoading = isLoading, errorMessage = errorMessage)
        }
        _errorMessage.addSource(charactersListing.networkState) { stateResource ->
            if (stateResource is ResourceError) {
                _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
            }
        }
    }

    private fun CharacterListViewState.isScreenLoading(newNetworkState: Resource<Unit>) =
            charactersList?.isEmpty() == true && newNetworkState is ResourceLoading<*>

    val viewState: LiveData<CharacterListViewState>
        get() = _viewState

    override fun openCharacter(id: Long) {
        _navigationAction.postValue(Event(CharacterListNavigationActions.OpenCharacterDetail(id)))
    }

    override fun retry() {
        charactersListing.retryAction()
    }

    override fun refresh() {
        if (_viewState.currentState.isRefreshing) return

        viewModelScope.launch {
            val refreshFlow = refreshCharactersUseCase()
            refreshFlow.collect { newResource ->
                _viewState.dispatchState { copy(isRefreshing = newResource is ResourceLoading) }

                if (newResource is ResourceError) {
                    _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
                }
            }
        }
    }

    private fun CharacterListViewState.getErrorMessage(newNetworkState: Resource<Unit>): String? {
        return if (newNetworkState is ResourceError && charactersList?.isEmpty() == true) {
            newNetworkState.error?.toString() ?: ""
        } else {
            null
        }
    }
}
