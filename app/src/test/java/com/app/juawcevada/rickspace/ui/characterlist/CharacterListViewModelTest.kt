package com.app.juawcevada.rickspace.ui.characterlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import arrow.core.Try
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.addStubs
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.observeTest
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CharacterListViewModel
    private lateinit var charactersList: MutableLiveData<Resource<List<Character>>>
    private lateinit var refreshUseCase: RefreshCharactersUseCase
    private lateinit var getUseCase: GetCharactersUseCase

    private val appTestDispatchers =
            AppDispatchers(
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined)

    @Before
    fun initViewModel() {

        charactersList = MutableLiveData()

        getUseCase = mock {
            on { invoke() } doReturn { charactersList }
        }
        refreshUseCase = mock {
            onBlocking { invoke(any()) } doReturn Try.Success(Unit)
        }

        viewModel =
                CharacterListViewModel(
                        getUseCase,
                        refreshUseCase,
                        appTestDispatchers).apply {
                    viewState.observeTest()
                    errorMessage.observeTest()
                    navigationAction.observeTest()
                }
    }

    @Test
    fun loadingEmptyList() {
        charactersList.value = ResourceLoading(data= emptyList())

        with(viewModel.viewState.value!!) {
            assertEquals(emptyList<Character>(), charactersList)
            assertEquals(true, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value) {
            assertNull(this)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun loadingNotEmptyList() {
        val characters = mutableListOf(character {})
        charactersList.value = ResourceSuccess(characters)

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value) {
            assertNull(this)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun successList() {
        val characters = mutableListOf(character {})
        charactersList.value = ResourceSuccess(characters)

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value) {
            assertNull(this)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun errorListNotEmpty() {
        val characters = mutableListOf(character {})
        charactersList.value = ResourceError(characters, NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value!!) {
            assertEquals(R.string.default_error_message, getContentIfNotHandled()!!.messageId)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun errorListEmpty() {
        charactersList.value = ResourceError(emptyList(), NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(emptyList<Character>(), charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertEquals("java.lang.NullPointerException", errorMessage)
        }

        with(viewModel.errorMessage.value!!) {
            assertEquals(R.string.default_error_message, getContentIfNotHandled()!!.messageId)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun refreshListSuccess() {
        val characters = mutableListOf(character {})
        charactersList.value = ResourceSuccess(characters)

        refreshUseCase.addStubs {
            onBlocking { invoke(Unit) } doReturn Try.Success(Unit)
        }

        viewModel.refresh()

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value) {
            assertNull(this)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }

    @Test
    fun refreshListError() {
        val characters = mutableListOf(character {})
        charactersList.value = ResourceSuccess(characters)
        refreshUseCase.addStubs {
            onBlocking { invoke(Unit) } doReturn Try.Failure(NullPointerException())
        }

        viewModel.refresh()

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value!!) {
            assertEquals(R.string.default_error_message, getContentIfNotHandled()!!.messageId)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }


    @Test
    fun retryAction() {
        viewModel.retry()

        charactersList.value = ResourceError(emptyList(), NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(emptyList<Character>(), charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertEquals("java.lang.NullPointerException", errorMessage)
        }

        with(viewModel.errorMessage.value!!) {
            assertEquals(R.string.default_error_message, getContentIfNotHandled()!!.messageId)
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }

        // Re-setup use case for reuse
        val characters = mutableListOf(character {})
        val retryCharactersList = MutableLiveData<Resource<List<Character>>>().apply {
            value = ResourceSuccess(characters)
        }

        whenever(getUseCase.invoke()) doReturn {retryCharactersList}

        viewModel.retry()

        with(viewModel.viewState.value!!) {
            assertEquals(characters, charactersList)
            assertEquals(false, isLoading)
            assertEquals(false, isRefreshing)
            assertNull(errorMessage)
        }

        with(viewModel.errorMessage.value!!) {
            assertNull(getContentIfNotHandled())
        }

        with(viewModel.navigationAction.value) {
            assertNull(this)
        }
    }


    @Test
    fun openCharacterAction() {
        viewModel.openCharacter(0)

        with(viewModel.navigationAction.value!!) {
            val event =
                    getContentIfNotHandled()!! as CharacterListNavigationActions.OpenCharacterDetail

            assertEquals(0, event.id)
        }
    }

}