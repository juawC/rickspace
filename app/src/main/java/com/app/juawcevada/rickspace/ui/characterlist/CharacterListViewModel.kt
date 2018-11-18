package com.app.juawcevada.rickspace.ui.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.toResource
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.domain.shared.runInScope
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.ScopedViewModel
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.app.juawcevada.rickspace.ui.shared.ViewStateLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenClassOnDebug
class CharacterListViewModel @Inject constructor(
        private val getCharactersUseCase: GetCharactersUseCase,
        private val refreshCharactersUseCase: RefreshCharactersUseCase,
        private val appDispatchers: AppDispatchers
) : ScopedViewModel(), CharacterListViewActions {

    private val fetchCharactersList = MutableLiveData<Unit>()

    private val characterResourceList: LiveData<Resource<List<Character>>> =
            Transformations.switchMap(fetchCharactersList) {
                runInScope { getCharactersUseCase() }
            }

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
        _viewState.addNewStateSource(characterResourceList) {
            val isEmptyState = it.data?.isEmpty() != false
            val isLoading = isEmptyState && it is ResourceLoading
            val errorMessage =
                    if (it is ResourceError && isEmptyState) {
                        it.error?.toString() ?: ""
                    } else {
                        null
                    }
            copy(
                    charactersList = it.data,
                    isLoading = isLoading,
                    errorMessage = errorMessage)
        }

        _viewState.addNewStateSource(refreshNetworkState) {
            copy(isRefreshing = it is ResourceLoading)
        }

        _errorMessage.addSource(characterResourceList) { stateResource ->
            if (stateResource is ResourceError) {
                _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
            }
        }

        _errorMessage.addSource(refreshNetworkState) { stateResource ->
            if (stateResource is ResourceError) {
                _errorMessage.value = Event(SnackbarMessage(R.string.default_error_message))
            }
        }
        fetchCharactersList.value = Unit
    }

    override fun openCharacter(id: Long) {
        _navigationAction.postValue(Event(CharacterListNavigationActions.OpenCharacterDetail(id)))
    }

    override fun retry() {
        fetchCharactersList.value = Unit
    }

    override fun refresh() {
        if (refreshNetworkState.value is ResourceLoading) {
            return
        } else {
            refreshNetworkState.value = ResourceLoading()
        }
        launch(appDispatchers.Main) {
            refreshNetworkState.value = refreshCharactersUseCase(Unit).toResource()
        }
    }
}
