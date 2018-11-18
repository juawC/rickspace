package com.app.juawcevada.rickspace.data.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import arrow.core.Try
import arrow.core.getOrElse
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import com.app.juawcevada.rickspace.data.shared.remote.RickAndMortyService
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.MockCallError
import com.app.juawcevada.rickspace.util.MockCallSuccess
import com.app.juawcevada.rickspace.util.builder.characterList
import com.app.juawcevada.rickspace.util.builder.characterListInfo
import com.app.juawcevada.rickspace.util.getValueTest
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import java.lang.Exception

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
    fun loadCharactersSuccess() {

        val characterRepository = buildRepository(
                apiServiceMock = mock {
                    on { getCharacters() } doReturn MockCallSuccess(
                            characterListInfo {
                                results {
                                   character { id { 1 } }
                                }
                            }
                    )
                },
                characterDaoMock = mock {
                    on { getAllCharacters() } doReturn MutableLiveData<List<Character>>().apply {
                        value = characterList {
                            character { id { 1 } }
                        }
                    }
                })

        runBlocking {
            val expectedList = characterList {
                character { id { 1 } }
            }
            val mockObserver: Observer<Resource<List<Character>>> = mock()
            characterRepository.getCharactersData(this).observeForever(mockObserver)
            verify(mockObserver).onChanged(ResourceLoading(expectedList))
            verify(mockObserver).onChanged(ResourceSuccess(expectedList))
        }
    }

    @Test
    fun loadCharactersError() {

        val characterRepository = buildRepository(
                apiServiceMock = mock {
                    on { getCharacters() } doReturn MockCallError(
                            404,
                            RealResponseBody(null, 0, Buffer()))
                },
                characterDaoMock = mock {
                    on { getAllCharacters() } doReturn MutableLiveData<List<Character>>().apply {
                        value = characterList {
                            character { id { 1 } }
                        }
                    }
                })

        runBlocking {
            val expectedList = characterList {
                character { id { 1 } }
            }
            val expectedResult = ResourceError(expectedList, Exception())
            val result = characterRepository.getCharactersData(this)
            val resultLoading = result.getValueTest()!!
            val resultError = result.getValueTest()!!
            assertTrue(resultLoading is ResourceLoading)
            assertTrue(resultError is ResourceError)
            assertEquals(expectedResult.data, result.getValueTest()!!.data)
        }
    }

    @Test
    fun refreshCharactersData() {
        val apiServiceMock: RickAndMortyService = mock {
            on { getCharacters() } doReturn MockCallSuccess(characterListInfo {})
        }

        val characterDaoMock: CharacterDao = mock ()

        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)
        runBlocking {
            assertEquals(Try.Success(Unit), characterRepository.refreshCharactersData())
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

        val characterDaoMock: CharacterDao = mock ()

        val characterRepository = buildRepository(apiServiceMock, characterDaoMock)
        runBlocking {

            assertThat(
                    characterRepository.refreshCharactersData().failed().getOrElse { it },
                    instanceOf(HttpException::class.java))
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
                appTestDispatchers)
    }
}