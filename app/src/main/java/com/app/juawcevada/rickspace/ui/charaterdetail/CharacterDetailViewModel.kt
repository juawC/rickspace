package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.juawcevada.rickspace.domain.character.GetCharacterUseCase
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.ViewStateLiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

@OpenClassOnDebug
class CharacterDetailViewModel @AssistedInject constructor(
    @Assisted private val characterId: Long,
    private val getCharacterUseCase: GetCharacterUseCase
) : ViewModel() {

    private val _viewState = ViewStateLiveData(CharacterDetailViewState()).apply {
        addNewStateSource(getCharacterUseCase(characterId)) {
            copy(character = it, characterEpisodes = it.episode)
        }
    }
    val viewState: LiveData<CharacterDetailViewState>
    get() = _viewState

    @AssistedInject.Factory
    interface Factory {
        fun create(characterId: Long): CharacterDetailViewModel
    }
}
