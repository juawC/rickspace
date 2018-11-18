package com.app.juawcevada.rickspace.domain.character

import androidx.lifecycle.LiveData
import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.domain.shared.UseCase
import com.app.juawcevada.rickspace.model.Character
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
        private val repository: CharacterRepository
) : UseCase<Unit, LiveData<Resource<List<Character>>>>() {

    operator fun invoke():CoroutineScope.() -> LiveData<Resource<List<Character>>> = invoke(Unit)

    override fun execute(coroutineScope: CoroutineScope, parameters: Unit): LiveData<Resource<List<Character>>> {
        return repository.getCharactersData(coroutineScope)
    }
}