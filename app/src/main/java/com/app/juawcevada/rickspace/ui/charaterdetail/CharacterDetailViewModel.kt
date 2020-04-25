package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.app.juawcevada.rickspace.domain.character.GetCharacterUseCase
import com.app.juawcevada.rickspace.extensions.setValueIfNew
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.ViewStateLiveData
import javax.inject.Inject


@OpenClassOnDebug
class CharacterDetailViewModel @Inject constructor(
        private val getCharacterUseCase: GetCharacterUseCase
) : ViewModel() {

    private val characterId = MutableLiveData<Long>()
    private val characterResult: LiveData<Character> = Transformations.switchMap(characterId) {
       getCharacterUseCase(it)
    }

    private val _viewState = ViewStateLiveData(CharacterDetailViewState()).apply {
        addNewStateSource(characterResult) {
            copy(character = it, characterEpisodes = it.episode)
        }
    }
    val viewState: LiveData<CharacterDetailViewState>
    get() = _viewState

    fun setCharacterId(id: Long) {
        characterId.setValueIfNew(id)
    }

}
