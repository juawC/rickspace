package com.app.juawcevada.rickspace.ui.characterlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.data.shared.repository.Listing
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.domain.character.GetCharactersUseCase
import com.app.juawcevada.rickspace.domain.character.RefreshCharactersUseCase
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.app.juawcevada.rickspace.util.TestCoroutineRule
import com.app.juawcevada.rickspace.util.TestDataSourceFactory
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.observeTest
import com.app.juawcevada.rickspace.util.toPagedList
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var listing: Listing<Character>
    private lateinit var refreshUseCase: RefreshCharactersUseCase
    private lateinit var viewModel: CharacterListViewModel
    private lateinit var listingRetryAction: () -> Unit
    private lateinit var listingPagedList: MutableLiveData<PagedList<Character>>
    private lateinit var listingNetworkState: MutableLiveData<Resource<Unit>>

    private lateinit var viewStateObserver: Observer<CharacterListViewState>
    private lateinit var errorMessageObserver: Observer<Event<SnackbarMessage>>
    private lateinit var navigationActionObserver: Observer<Event<CharacterListNavigationActions>>

    @Before
    fun initViewModel() {
        listingRetryAction = mock()
        listingPagedList = MutableLiveData()
        listingNetworkState = MutableLiveData()
        refreshUseCase = mock()

        listing = Listing(listingPagedList, listingNetworkState, listingRetryAction)

        val getUseCase: GetCharactersUseCase = mock {
            on { invoke(any()) } doReturn listing
        }

        viewModel = CharacterListViewModel(getUseCase, refreshUseCase).apply {
            viewStateObserver = viewState.observeTest()
            errorMessageObserver = errorMessage.observeTest()
            navigationActionObserver = navigationAction.observeTest()
        }
    }

    @After
    fun clearViewModel() {
        viewModel.apply {
            viewState.removeObserver(viewStateObserver)
            errorMessage.removeObserver(errorMessageObserver)
            navigationAction.removeObserver(navigationActionObserver)
        }
    }

    @Test
    fun loadingEmptyList() {
        listingPagedList.value = TestDataSourceFactory().buildPagedList()
        listingNetworkState.value = ResourceLoading()

        val viewStateLoading = CharacterListViewState(
                isLoading = true,
                isRefreshing = false,
                charactersList = mutableListOf<Character>().toPagedList(),
                errorMessage = null
        )
        assertEquals(viewStateLoading, viewModel.viewState.value)
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun loadingNotEmptyList() {
        val characters = mutableListOf(character {})
        listingPagedList.value = characters.toPagedList()
        listingNetworkState.value = ResourceLoading()

        val viewStateSuccess = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        assertEquals(viewStateSuccess, viewModel.viewState.value)
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun successList() {
        val characters = mutableListOf(character {})
        listingPagedList.value = characters.toPagedList()
        listingNetworkState.value = ResourceSuccess()

        val viewStateSuccess = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        assertEquals(viewStateSuccess, viewModel.viewState.value)
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun errorListNotEmpty() {
        val characters = mutableListOf(character {})
        listingPagedList.value = characters.toPagedList()
        listingNetworkState.value = ResourceError(error = NullPointerException())

        val viewStateError = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        assertEquals(viewStateError, viewModel.viewState.value)
        assertEquals(Event(SnackbarMessage(R.string.default_error_message)), viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun errorListEmpty() {
        listingPagedList.value = TestDataSourceFactory().buildPagedList()
        listingNetworkState.value = ResourceError(error = NullPointerException())

        val viewStateError = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = mutableListOf<Character>().toPagedList(),
                errorMessage = "java.lang.NullPointerException"
        )
        assertEquals(viewStateError, viewModel.viewState.value)
        assertEquals(Event(SnackbarMessage(R.string.default_error_message)), viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun refreshListSuccess() = testCoroutineRule.runBlockingTest {
        whenever(refreshUseCase.invoke(Unit)).thenReturn(flow {
            emit(ResourceLoading())
            emit(ResourceSuccess())
        })
        val characters = mutableListOf(character {})
        listingPagedList.value = characters.toPagedList()
        listingNetworkState.value = ResourceSuccess()
        viewModel.refresh()

        val viewStateLoading = CharacterListViewState(
                isLoading = false,
                isRefreshing = true,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        val viewStateSuccess = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        inOrder(viewStateObserver) {
            verify(viewStateObserver).onChanged(viewStateLoading)
            verify(viewStateObserver).onChanged(viewStateSuccess)
        }
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun refreshListError() = testCoroutineRule.runBlockingTest {
        val characters = mutableListOf(character {})
        listingPagedList.value = characters.toPagedList()
        listingNetworkState.value = ResourceSuccess()
        whenever(refreshUseCase.invoke(Unit)).thenReturn(flow<Resource<Unit>> { emit(ResourceError(error = NullPointerException())) })

        viewModel.refresh()

        val viewStateError = CharacterListViewState(
                isLoading = false,
                isRefreshing = false,
                charactersList = characters.toPagedList(),
                errorMessage = null
        )
        assertEquals(viewStateError, viewModel.viewState.value)
        assertEquals(Event(SnackbarMessage(R.string.default_error_message)), viewModel.errorMessage.value)
        assertNull(viewModel.navigationAction.value)
    }

    @Test
    fun retryAction() {
        viewModel.retry()
        verify(listingRetryAction).invoke()
    }


    @Test
    fun openCharacterAction() {
        viewModel.openCharacter(0)

        assertEquals(Event(CharacterListNavigationActions.OpenCharacterDetail(0)), viewModel.navigationAction.value)
    }

}