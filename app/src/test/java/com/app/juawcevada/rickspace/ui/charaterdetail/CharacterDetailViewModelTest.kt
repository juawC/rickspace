package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.app.juawcevada.rickspace.domain.character.GetCharacterUseCase
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.builder.episodes
import com.app.juawcevada.rickspace.util.observeTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var characterLiveData: MutableLiveData<Character>
    private lateinit var viewModel: CharacterDetailViewModel

    @Before
    fun initViewModel() {
        characterLiveData = MutableLiveData()

        val getCharacterUseCase: GetCharacterUseCase = mock {
            on { invoke(any()) } doReturn { characterLiveData }
        }

        viewModel = CharacterDetailViewModel(getCharacterUseCase).apply {
            viewState.observeTest()
        }
    }

    @Test
    fun loadCharacter() {
        val testCharacter = character {
            episodes {
                episode { "1" }
                episode { "2" }
                episode { "3" }
            }
        }
        characterLiveData.value = testCharacter

        viewModel.setCharacterId(0)

        with(viewModel.viewState.value!!) {
            assertFalse(isRefreshing)
            assertNull(errorMessage)
            assertEquals(testCharacter, character)
            assertEquals(
                    episodes {
                        episode { "1" }
                        episode { "2" }
                        episode { "3" }
                    },
                    characterEpisodes
            )

        }
    }
}