package com.app.juawcevada.rickspace.data.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.util.getValueTest
import com.app.juawcevada.rickspace.util.observeTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SingleResourceLoaderTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testSuccessSingleLoad() {
        val singleResourceLoader = SingleResourceLoader().apply {
            currentState.observeTest()
        }
        val dummyLiveData = MutableLiveData<Resource<Unit>>()

        singleResourceLoader.loadData { dummyLiveData }

        dummyLiveData.postValue(ResourceSuccess())

        assertEquals(singleResourceLoader.currentState.getValueTest(), ResourceSuccess<Unit>())
    }

    @Test
    fun testSuccessConcurrentLoad() {
        val singleResourceLoader = SingleResourceLoader().apply {
            currentState.observeTest()
        }

        val dummyLiveData1 = MutableLiveData<Resource<Unit>>()
        val dummyLiveData2 = MutableLiveData<Resource<Unit>>()

        singleResourceLoader.loadData { dummyLiveData1 }

        dummyLiveData1.postValue(ResourceLoading())

        // Ignores this load data because it is already loading
        singleResourceLoader.loadData { dummyLiveData2 }

        dummyLiveData2.postValue(ResourceSuccess())

        assertEquals(singleResourceLoader.currentState.getValueTest(), ResourceLoading<Unit>())
    }

    @Test
    fun testSuccessSequentialLoad() {
        val singleResourceLoader = SingleResourceLoader().apply {
            currentState.observeTest()
        }
        val dummyLiveData1 = MutableLiveData<Resource<Unit>>()
        val dummyLiveData2 = MutableLiveData<Resource<Unit>>()

        singleResourceLoader.loadData { dummyLiveData1 }

        dummyLiveData1.postValue(ResourceError())

        // Don't ignore this load data because the previous one has already finished
        singleResourceLoader.loadData { dummyLiveData2 }

        dummyLiveData2.postValue(ResourceLoading())

        assertEquals(singleResourceLoader.currentState.getValueTest(), ResourceLoading<Unit>())
    }

    @Test
    fun testNoObserversLoad() {
        val singleResourceLoader = SingleResourceLoader()

        val dummyLiveData1 = MutableLiveData<Resource<Unit>>()
        val dummyLiveData2 = MutableLiveData<Resource<Unit>>()

        singleResourceLoader.loadData { dummyLiveData1 }

        dummyLiveData1.postValue(ResourceError())

        // Ignore this load data because as the LiveData is not being observed there
        // is no way to know what happened with the last load data so no new sources are added
        singleResourceLoader.loadData { dummyLiveData2 }

        dummyLiveData2.postValue(ResourceLoading())

        assertEquals(singleResourceLoader.currentState.getValueTest(), ResourceError<Unit>())
    }

}