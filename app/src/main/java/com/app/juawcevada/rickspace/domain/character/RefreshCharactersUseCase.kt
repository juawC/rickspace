package com.app.juawcevada.rickspace.domain.character

import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.domain.shared.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) : UseCase<Unit, Flow<Resource<Unit>>>() {

    override fun execute(
        parameters: Unit
    ): Flow<Resource<Unit>> {

        return repository.refreshCharactersData()
    }
}