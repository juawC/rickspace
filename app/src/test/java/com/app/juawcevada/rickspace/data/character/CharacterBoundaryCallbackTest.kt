package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.util.builder.character
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class CharacterBoundaryCallbackTest {

    @Test
    fun onZeroItemsLoaded() {
        val repositoryMock: CharacterRepository = mock()
        val characterBoundaryCallBack = createCharacterBoundaryCallBack(repositoryMock)

        characterBoundaryCallBack.onZeroItemsLoaded()

        verify(repositoryMock).loadCharactersFirstPage()
    }

    @Test
    fun onItemAtEndLoaded() {
        val repositoryMock: CharacterRepository = mock()
        val characterBoundaryCallBack = createCharacterBoundaryCallBack(repositoryMock)
        val character = character {}

        characterBoundaryCallBack.onItemAtEndLoaded(character)

        verify(repositoryMock).loadCharactersNextPage(eq(character))
    }


    private fun createCharacterBoundaryCallBack(
            characterRepository: CharacterRepository = mock()
    ): CharacterBoundaryCallback {

        val singleResourceLoader: SingleResourceLoader = mock {
            on { loadData(any()) } doAnswer { invocationOnMock ->
                (invocationOnMock.arguments[0] as () -> LiveData<Resource<Unit>>).invoke()
                Unit
            }
        }

        return CharacterBoundaryCallback(characterRepository, singleResourceLoader
        )
    }
}