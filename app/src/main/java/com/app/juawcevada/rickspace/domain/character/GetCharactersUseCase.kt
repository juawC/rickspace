package com.app.juawcevada.rickspace.domain.character

import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.data.shared.repository.Listing
import com.app.juawcevada.rickspace.domain.shared.UseCase
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
        private val repository: CharacterRepository
) : UseCase<Unit, Listing<Character>>() {

    operator fun invoke():CoroutineScope.() -> Listing<Character> = invoke(Unit)

    override fun execute(coroutineScope: CoroutineScope, parameters: Unit): Listing<Character> {
        return repository.getCharactersData(coroutineScope)
    }
}