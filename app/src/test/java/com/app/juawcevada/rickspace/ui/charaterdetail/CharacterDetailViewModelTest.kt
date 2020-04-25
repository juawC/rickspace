package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.app.juawcevada.rickspace.domain.character.GetCharacterUseCase
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListViewState
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.builder.episodes
import com.app.juawcevada.rickspace.util.observeTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var characterLiveData: MutableLiveData<Character>
    private lateinit var viewModel: CharacterDetailViewModel

    private lateinit var viewStateObserver: Observer<CharacterDetailViewState>

    @Before
    fun initViewModel() {
        characterLiveData = MutableLiveData()

        val getCharacterUseCase: GetCharacterUseCase = mock {
            on { invoke(any()) } doReturn characterLiveData
        }

        viewModel = CharacterDetailViewModel(getCharacterUseCase).apply {
            viewStateObserver = viewState.observeTest()
        }
    }

    @After
    fun clearViewModel() {
        viewModel.apply {
            viewState.removeObserver(viewStateObserver)
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

        val viewStateSuccess = CharacterDetailViewState(
                isRefreshing = false,
                character = testCharacter,
                characterEpisodes = testCharacter.episode,
                errorMessage = null
        )
        assertEquals(viewStateSuccess, viewModel.viewState.value)
    }
}