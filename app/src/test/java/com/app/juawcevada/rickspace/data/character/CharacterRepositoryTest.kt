package com.app.juawcevada.rickspace.data.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.TestCoroutineRule
import com.app.juawcevada.rickspace.util.TestDataSourceFactory
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.builder.characterListInfo
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class CharacterRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()


    private val appTestDispatchers =
            AppDispatchers(
                    testCoroutineRule.testCoroutineDispatcher,
                    testCoroutineRule.testCoroutineDispatcher,
                    testCoroutineRule.testCoroutineDispatcher
            )

    @Test
    fun loadCharactersFirstPagedList() = testCoroutineRule.runBlockingTest {
        val testDataSourceFactory = TestDataSourceFactory()
        val apiServiceMock: RickAndMortyService = mock {
            onBlocking { getCharacters() } doReturn characterListInfo {}
        }
        val characterDaoMock: CharacterDao = mock {
            on { getAllCharacters() } doReturn testDataSourceFactory
            on { getNextIndexCharacter() } doReturn 0
        }
        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)

        val listing = characterRepository.getCharactersData()

        val mockObserverPagedList: Observer<PagedList<Character>> = mock()
        val mockObserverNetworkState: Observer<Resource<Unit>> = mock()
        listing.pagedList.observeForever(mockObserverPagedList)
        listing.networkState.observeForever(mockObserverNetworkState)

        inOrder(mockObserverPagedList) {
            verify(mockObserverPagedList).onChanged(testDataSourceFactory.buildPagedList())
        }
        inOrder(mockObserverNetworkState) {
            verify(mockObserverNetworkState).onChanged(ResourceLoading())
            verify(mockObserverNetworkState).onChanged(ResourceSuccess())
        }
        inOrder(apiServiceMock) {
            verify(apiServiceMock).getCharacters()
        }

        listing.pagedList.removeObserver(mockObserverPagedList)
        listing.networkState.removeObserver(mockObserverNetworkState)
    }

    @Test
    fun loadCharactersNextPage() = testCoroutineRule.runBlockingTest {
        val testDataSourceFactory =
                TestDataSourceFactory(
                        mutableListOf(
                                character { id { 1 } },
                                character { id { 2 } },
                                character { id { 3 } },
                                character { id { 4 } },
                                character { id { 5 } },
                                character { id { 6 } },
                                character { id { 7 } },
                                character { id { 8 } },
                                character { id { 9 } },
                                character {
                                    id { 10 }
                                    nextPage { 1 }
                                }

                        ))

        val apiServiceMock: RickAndMortyService = mock {
            onBlocking { getCharactersByPage(any()) } doReturn characterListInfo {}
        }
        val characterDaoMock: CharacterDao = mock {
            on { getAllCharacters() } doReturn testDataSourceFactory
            on { getNextIndexCharacter() } doReturn 0
        }
        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)

        val listing = characterRepository.getCharactersData()
        val mockObserverPagedList: Observer<PagedList<Character>> = mock()
        val mockObserverNetworkState: Observer<Resource<Unit>> = mock()
        listing.pagedList.observeForever(mockObserverPagedList)
        listing.networkState.observeForever(mockObserverNetworkState)

        inOrder(mockObserverPagedList) {
            verify(mockObserverPagedList).onChanged(testDataSourceFactory.buildPagedList())
        }
        inOrder(mockObserverNetworkState) {
            verify(mockObserverNetworkState).onChanged(ResourceLoading())
            verify(mockObserverNetworkState).onChanged(ResourceSuccess())
        }
        inOrder(apiServiceMock) {
            verify(apiServiceMock).getCharactersByPage(1)
        }

        listing.pagedList.removeObserver(mockObserverPagedList)
        listing.networkState.removeObserver(mockObserverNetworkState)
    }

    @Test
    fun refreshCharactersData() = testCoroutineRule.runBlockingTest {
        val apiServiceMock: RickAndMortyService = mock {
            onBlocking { getCharacters() } doReturn characterListInfo {}
        }
        val characterDaoMock: CharacterDao = mock {
            on { getNextIndexCharacter() } doReturn 0
        }
        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)

        val networkStateList = characterRepository.refreshCharactersData().toList()

        assertEquals(
                listOf(ResourceLoading<Unit>(), ResourceSuccess<Unit>()),
                networkStateList
        )
        inOrder(characterDaoMock) {
            verify(characterDaoMock).deleteAllCharacters()
            verify(characterDaoMock).insertAll(emptyList())
        }
    }

    @Test
    fun refreshCharactersError() = testCoroutineRule.runBlockingTest {
        val errorException = HttpException(createResponseError<CharacterListInfo>(
                404,
                RealResponseBody(null, 0, Buffer()))
        )
        val apiServiceMock: RickAndMortyService = mock {
            onBlocking { getCharacters() } doThrow errorException
        }
        val characterDaoMock: CharacterDao = mock {
            on { getNextIndexCharacter() } doReturn 0
        }
        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)

        val networkStateList = characterRepository.refreshCharactersData().toList()

        assertEquals(
                listOf(ResourceLoading<Unit>(), ResourceError<Unit>(error = errorException)),
                networkStateList
        )
        verify(characterDaoMock, never()).deleteAllCharacters()
        verify(characterDaoMock, never()).insertAll(emptyList())
    }


    private fun buildRepository(
        apiServiceMock: RickAndMortyService = mock(),
        characterDaoMock: CharacterDao = mock()): CharacterRepository {

        val appDatabaseMock: AppDatabase = mock {
            on { characterDao() } doReturn characterDaoMock

            on { runInTransaction(any()) } doAnswer { invocationOnMock ->
                (invocationOnMock.arguments[0] as Runnable).run()
            }
        }

        return CharacterRepository(
                appDatabaseMock,
                apiServiceMock,
                10,
                appTestDispatchers)
    }

    private fun <T> createResponseError(
        code: Int, body: okhttp3.ResponseBody
    ): Response<T> = Response.error<T>(code, body)
}