package com.app.juawcevada.rickspace.ui.characterlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.*
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.TestDataSourceFactory
import com.app.juawcevada.rickspace.util.model.builder.character
import com.app.juawcevada.rickspace.util.observeTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.NullPointerException
import java.util.concurrent.Executor

class CharacterListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var listing: Listing<Character>
    private lateinit var refreshNetworkState: MutableLiveData<Resource<Unit>>
    private lateinit var viewModel: CharacterListViewModel
    private lateinit var listingRetryAction: () -> Unit
    private lateinit var listingPagedList: MutableLiveData<PagedList<Character>>
    private lateinit var listingNetworkState: MutableLiveData<Resource<Unit>>


    @Before
    fun initViewModel() {
        listingRetryAction = mock()
        listingPagedList  =MutableLiveData()
        listingNetworkState = MutableLiveData()
        refreshNetworkState = MutableLiveData()

        listing = Listing(listingPagedList, listingNetworkState, listingRetryAction)

        val getUseCase: GetCharactersUseCase = mock {
            on { invoke(any()) } doReturn { listing }
        }
        val refreshUseCase: RefreshCharactersUseCase = mock {
            on { invoke(any()) } doReturn { refreshNetworkState }
        }

        viewModel = CharacterListViewModel(getUseCase, refreshUseCase).apply {
            viewState.observeTest()
            errorMessage.observeTest()
            navigationAction.observeTest()
        }
    }

    @Test
    fun loadingEmptyList() {
        listingPagedList.value = createPagedList()
        listingNetworkState.value = ResourceLoading()

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
        listingPagedList.value = createPagedList(characters)
        listingNetworkState.value = ResourceLoading()

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
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
        listingPagedList.value = createPagedList(characters)
        listingNetworkState.value = ResourceSuccess()

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
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
        listingPagedList.value = createPagedList(characters)
        listingNetworkState.value = ResourceError(error = NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
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
        listingPagedList.value = createPagedList()
        listingNetworkState.value = ResourceError(error = NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(emptyList<Character>() , charactersList)
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
        listingPagedList.value = createPagedList(characters)
        listingNetworkState.value = ResourceSuccess()

        viewModel.refresh()
        refreshNetworkState.value = ResourceLoading()

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
            assertEquals(false, isLoading)
            assertEquals(true, isRefreshing)
            assertNull(errorMessage)
        }

        refreshNetworkState.value = ResourceSuccess()

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
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
        listingPagedList.value = createPagedList(characters)
        listingNetworkState.value = ResourceSuccess()

        viewModel.refresh()
        refreshNetworkState.value = ResourceError(error = NullPointerException())

        with(viewModel.viewState.value!!) {
            assertEquals(characters , charactersList)
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
        verify(listingRetryAction).invoke()
    }


    @Test
    fun openCharacterAction() {
        viewModel.openCharacter(0)

        with(viewModel.navigationAction.value!!) {
            val event =
                    getContentIfNotHandled()!! as CharacterListNavigationActions.OpenCharacterDetail

            assertEquals(0, event.id )
        }
    }

    private fun createPagedList(
            characterList: MutableList<Character> = mutableListOf()
    ): PagedList<Character> {
        val instantExecutor = Executor { it.run() }
        val testDataSourceFactory = TestDataSourceFactory(characterList)
        val pagingConfig = PagedList.Config.Builder().setPageSize(1).build()

        return PagedList
                .Builder(testDataSourceFactory.create(), pagingConfig)
                .setNotifyExecutor(instantExecutor)
                .setFetchExecutor(instantExecutor)
                .build()
    }
}