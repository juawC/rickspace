package com.app.juawcevada.rickspace.domain.character

import arrow.core.Try
import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.domain.shared.SuspendUseCase
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(
        private val repository: CharacterRepository
) : SuspendUseCase<Unit, Try<Unit>>() {

    override suspend fun execute(
            parameters: Unit
    ): Try<Unit> {

        return repository.refreshCharactersData()
    }
}