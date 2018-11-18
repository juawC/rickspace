package com.app.juawcevada.rickspace.data.shared.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.app.juawcevada.rickspace.data.character.CharacterListInfo
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.util.MockCallError
import com.app.juawcevada.rickspace.util.MockCallSuccess
import com.app.juawcevada.rickspace.util.builder.characterList
import com.app.juawcevada.rickspace.util.builder.characterListInfo
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import java.util.*

class LiveDataDataSourceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val charactersRemote =
            characterListInfo {
                results {
                    character { id { 1 } }
                }
            }

    private val charactersLocal =
            characterList {
                character { id { 0 } }
            }

    private val mockSaveCall: (CharacterListInfo) -> Unit = mock()

    private val appTestDispatchers =
            AppDispatchers(
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined,
                    Dispatchers.Unconfined)

    @Test
    fun emptyLocalSourceErrorRemote()  = runBlocking {
        val testLiveDataDataSource = createLiveDataDataSource(
                this,
                appTestDispatchers,
                ::remoteFetchCallError,
                ::localFetchCallEmpty,
                mockSaveCall,
                ::isNewDataRequired)

        val mockObserver: Observer<Resource<List<Character>>> = mock()
        testLiveDataDataSource.observeForever(mockObserver)

        inOrder(mockObserver) {
            verify(mockObserver).onChanged(ResourceLoading(emptyList()))
            verify(mockObserver).onChanged(any<ResourceError<List<Character>>>())
        }

        verifyNoMoreInteractions(mockSaveCall)
        verifyNoMoreInteractions(mockObserver)
    }

    @Test
    fun emptyLocalSourceSuccessRemote() = runBlocking  {
        val localResponseQueue: Queue<LiveData<List<Character>>> =
                ArrayDeque<LiveData<List<Character>>>().apply {
                    add (localFetchCallEmpty())
                    add (localFetchCallSuccess())
                }

        val testLiveDataDataSource = createLiveDataDataSource(
                this,
                appTestDispatchers,
                ::remoteFetchCallSuccess,
                {localResponseQueue.poll()},
                mockSaveCall,
                ::isNewDataRequired)

        val mockObserver: Observer<Resource<List<Character>>> = mock()
        testLiveDataDataSource.observeForever(mockObserver)

        inOrder(mockObserver) {
            verify(mockObserver).onChanged(ResourceLoading(emptyList()))
            verify(mockObserver).onChanged(ResourceSuccess(charactersLocal))
        }

        verify(mockSaveCall).invoke(charactersRemote)

        verifyNoMoreInteractions(mockSaveCall)
        verifyNoMoreInteractions(mockObserver)
    }

    @Test
    fun successLocalSourceSuccessRemote() = runBlocking {
        val testLiveDataDataSource = createLiveDataDataSource(
                this,
                appTestDispatchers,
                ::remoteFetchCallSuccess,
                ::localFetchCallSuccess,
                mockSaveCall,
                ::isNewDataRequired)

        val mockObserver: Observer<Resource<List<Character>>> = mock()
        testLiveDataDataSource.observeForever(mockObserver)

        inOrder(mockObserver) {
            verify(mockObserver).onChanged(ResourceLoading(charactersLocal))
            verify(mockObserver).onChanged(ResourceSuccess(charactersLocal))
        }

        verify(mockSaveCall).invoke(charactersRemote)

        verifyNoMoreInteractions(mockSaveCall)
        verifyNoMoreInteractions(mockObserver)
    }

    @Test
    fun successLocalSourceNoRemote()  = runBlocking {
        val testLiveDataDataSource = createLiveDataDataSource(
                this,
                appTestDispatchers,
                ::remoteFetchCallSuccess,
                ::localFetchCallSuccess,
                mockSaveCall,
                ::isNewDataNotRequired)

        val mockObserver: Observer<Resource<List<Character>>> = mock()
        testLiveDataDataSource.observeForever(mockObserver)

        verify(mockObserver).onChanged(ResourceSuccess(charactersLocal))

        verifyNoMoreInteractions(mockSaveCall)
        verifyNoMoreInteractions(mockObserver)
    }

    @Test
    fun successLocalSourceErrorRemote()  = runBlocking {
        val testLiveDataDataSource = createLiveDataDataSource(
                this,
                appTestDispatchers,
                ::remoteFetchCallError,
                ::localFetchCallSuccess,
                mockSaveCall,
                ::isNewDataRequired)

        val mockObserver: Observer<Resource<List<Character>>> = mock()
        testLiveDataDataSource.observeForever(mockObserver)

        inOrder(mockObserver) {
            verify(mockObserver).onChanged(ResourceLoading(charactersLocal))
            verify(mockObserver).onChanged(any<ResourceError<List<Character>>>())
        }

        verifyNoMoreInteractions(mockSaveCall)
        verifyNoMoreInteractions(mockObserver)
    }

    fun isNewDataNotRequired(data: List<Character>?) = false

    fun isNewDataRequired(data: List<Character>?) = true

    fun remoteFetchCallSuccess(): Call<CharacterListInfo> {
        return MockCallSuccess(
                characterListInfo {
                    results {
                        character { id { 1 } }
                    }
                }
        )
    }

    fun localFetchCallSuccess(): LiveData<List<Character>> {
        return MutableLiveData<List<Character>>().apply {
            value = characterList {
                character { id { 0 } }
            }
        }
    }

    fun localFetchCallEmpty(): LiveData<List<Character>> {
        return MutableLiveData<List<Character>>().apply {
            value = emptyList()
        }
    }

    fun remoteFetchCallError(): Call<CharacterListInfo> {
        return MockCallError(
                404,
                RealResponseBody(null, 0, Buffer()))
    }
}

