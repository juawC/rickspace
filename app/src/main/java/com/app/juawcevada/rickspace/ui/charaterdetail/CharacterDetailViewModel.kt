package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.app.juawcevada.rickspace.domain.character.GetCharacterUseCase
import com.app.juawcevada.rickspace.domain.shared.runInScope
import com.app.juawcevada.rickspace.extensions.setValueIfNew
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.shared.ScopedViewModel
import javax.inject.Inject


class CharacterDetailViewModel @Inject constructor(
        private val getCharacterUseCase: GetCharacterUseCase
) : ScopedViewModel() {

    private val characterId = MutableLiveData<Long>()
    private val characterResult: LiveData<Character> = Transformations.switchMap(characterId) {
        runInScope { getCharacterUseCase(it) }
    }

    val viewState = MediatorLiveData<CharacterDetailViewState>().apply {
        value = CharacterDetailViewState()
    }

    init {
        viewState.addSource(characterResult) {
            viewState.value = viewState.value?.copy(character = it, characterEpisodes = it.episode)
        }
    }

    fun setCharacterId(id: Long) {
        characterId.setValueIfNew(id)
    }

}
