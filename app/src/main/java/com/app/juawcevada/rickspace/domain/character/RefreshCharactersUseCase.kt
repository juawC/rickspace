package com.app.juawcevada.rickspace.domain.character

import androidx.lifecycle.LiveData
import com.app.juawcevada.rickspace.data.character.CharacterRepository
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.domain.shared.UseCase
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(
        private val repository: CharacterRepository
) : UseCase<Unit, LiveData<Resource<Unit>>>() {

    override fun execute(parameters: Unit): LiveData<Resource<Unit>> {
        return repository.refreshCharactersData(parentJob)
    }
}