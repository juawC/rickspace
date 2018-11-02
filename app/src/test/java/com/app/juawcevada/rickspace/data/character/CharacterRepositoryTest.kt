package com.app.juawcevada.rickspace.data.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.util.MockCallError
import com.app.juawcevada.rickspace.util.MockCallSuccess
import com.app.juawcevada.rickspace.util.TestDataSourceFactory
import com.app.juawcevada.rickspace.util.getValueTest
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.builder.characterListInfo
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val appTestDispatchers =
            AppDispatchers(
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined)

    @Test
    fun loadCharactersFirstPage() {
        val testDataSourceFactory = TestDataSourceFactory()
        val characterRepository = buildRepository(
                apiServiceMock = mock {
                    on { getCharacters() } doReturn MockCallSuccess(characterListInfo {})
                },
                characterDaoMock = mock {
                    on { getAllCharacters() } doReturn testDataSourceFactory
                    on { getNextIndexCharacter() } doReturn 0
                })

        runBlocking {
            val listing = characterRepository.getCharactersData(this)
            val pagedList = listing.pagedList.getValueTest()!!
            assertEquals(0, pagedList.size)
        }
    }

    @Test
    fun loadCharactersNextPage() {
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
            on { getCharactersByPage(any()) } doReturn MockCallSuccess(characterListInfo {})
        }

        val characterDaoMock: CharacterDao = mock {
            on { getAllCharacters() } doReturn testDataSourceFactory
            on { getNextIndexCharacter() } doReturn 0
        }

        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)

        runBlocking {
            val listing = characterRepository.getCharactersData(this)
            val pagedList = listing.pagedList.getValueTest()!!
            assertEquals(10, pagedList.size)
            verify(apiServiceMock).getCharactersByPage(1)
            verify(characterDaoMock).insertAll(any())
        }
    }

    @Test
    fun refreshCharactersData() {
        val apiServiceMock: RickAndMortyService = mock {
            on { getCharacters() } doReturn MockCallSuccess(characterListInfo {})
        }

        val characterDaoMock: CharacterDao = mock {
            on { getNextIndexCharacter() } doReturn 0
        }

        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)
        runBlocking {
            val networkState = characterRepository.refreshCharactersData(this)

            assertEquals(ResourceSuccess(Unit), networkState.getValueTest())
            inOrder(characterDaoMock) {
                verify(characterDaoMock).deleteAllCharacters()
                verify(characterDaoMock).insertAll(any())
            }
        }
    }

    @Test
    fun refreshCharactersError() {
        val errorResponse =
                MockCallError<CharacterListInfo>(
                        404,
                        RealResponseBody(null, 0, Buffer()))

        val apiServiceMock: RickAndMortyService = mock {
            on { getCharacters() } doReturn errorResponse
        }

        val characterDaoMock: CharacterDao = mock {
            on { getNextIndexCharacter() } doReturn 0
        }

        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)
        runBlocking {
            val networkState = characterRepository.refreshCharactersData(this)

            assertThat(networkState.getValueTest(), instanceOf(ResourceError<Unit>()::class.java))
            verify(characterDaoMock, never()).deleteAllCharacters()
            verify(characterDaoMock, never()).insertAll(any())
        }
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
}