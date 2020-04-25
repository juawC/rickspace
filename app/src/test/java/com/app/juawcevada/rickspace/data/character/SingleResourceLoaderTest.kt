package com.app.juawcevada.rickspace.data.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import com.app.juawcevada.rickspace.util.TestCoroutineRule
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SingleResourceLoaderTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var singleResourceLoader: SingleResourceLoader
    private lateinit var currentStateObserver: Observer<Resource<Unit>>

    @Before
    fun init() {
        singleResourceLoader = SingleResourceLoader()
        currentStateObserver = mock()
        singleResourceLoader.currentState.observeForever(currentStateObserver)
    }

    @After
    fun cleanup() {
        singleResourceLoader.currentState.removeObserver(currentStateObserver)
    }

    @Test
    fun testLoadData() = testCoroutineRule.runBlockingTest {
        singleResourceLoader.loadData {
            flow {
                emit(ResourceLoading())
                emit(ResourceSuccess())
            }
        }

        inOrder(currentStateObserver) {
            verify(currentStateObserver).onChanged(ResourceLoading())
            verify(currentStateObserver).onChanged(ResourceSuccess())
        }
    }

    @Test
    fun testLoadDataIgnoreWhileLoading() = testCoroutineRule.runBlockingTest {
        singleResourceLoader.loadData {
            flow<Resource<Unit>> {
                emit(ResourceLoading())
            }
        }

        singleResourceLoader.loadData {
            flow<Resource<Unit>> {
                emit(ResourceLoading())
            }
        }

        inOrder(currentStateObserver) {
            verify(currentStateObserver,times(1)).onChanged(ResourceLoading())
        }
    }

    @Test
    fun testRetry() = testCoroutineRule.runBlockingTest {
        singleResourceLoader.loadData {
            flow {
                emit(ResourceLoading())
                emit(ResourceSuccess())
            }
        }
        singleResourceLoader.retryLastAction()

        inOrder(currentStateObserver) {
            verify(currentStateObserver).onChanged(ResourceLoading())
            verify(currentStateObserver).onChanged(ResourceSuccess())
            verify(currentStateObserver).onChanged(ResourceLoading())
            verify(currentStateObserver).onChanged(ResourceSuccess())
        }
    }
}