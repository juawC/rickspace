package com.app.juawcevada.rickspace.domain.character

import androidx.lifecycle.LiveData
import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.domain.shared.UseCase
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GetCharacterUseCase @Inject constructor(
        private val repository: CharacterRepository
) : UseCase<Long, LiveData<Character>>() {

    override fun execute(coroutineScope: CoroutineScope,  parameters: Long): LiveData<Character> {
        return repository.getCharacterData(parameters)
    }
}