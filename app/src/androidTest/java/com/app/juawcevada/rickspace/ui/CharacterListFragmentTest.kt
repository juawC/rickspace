package com.app.juawcevada.rickspace.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.testing.SingleFragmentActivity
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListFragment
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListNavigationActions
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListViewModel
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListViewState
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.ui.shared.FragmentBindingAdapters
import com.app.juawcevada.rickspace.ui.shared.FragmentDataBindingComponent
import com.app.juawcevada.rickspace.util.createTestFactory

@RunWith(AndroidJUnit4::class)
class CharacterListFragmentTest {

    @get:Rule
    val activityRule =
            ActivityTestRule(
                    SingleFragmentActivity::class.java,
                    true,
                    true)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var characterListViewModel: CharacterListViewModel
    private lateinit var navigationAction: MutableLiveData<Event<CharacterListNavigationActions>>
    private lateinit var errorMessage: MutableLiveData<Event<SnackbarMessage>>
    private lateinit var viewState: MutableLiveData<CharacterListViewState>
    private lateinit var fragment: CharacterListFragment
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    @Before
    fun initViewModel() {
        navigationAction = MutableLiveData()
        errorMessage = MutableLiveData()
        viewState = MutableLiveData()
        fragment = CharacterListFragment()

        mockBindingAdapter = mock()

        characterListViewModel = mock {
            on {viewState} doReturn this@CharacterListFragmentTest.viewState
            on {errorMessage} doReturn this@CharacterListFragmentTest.errorMessage
            on {navigationAction} doReturn this@CharacterListFragmentTest.navigationAction
        }

        fragment.viewModelFactory = characterListViewModel.createTestFactory()

        fragment.fragmentDataBindingComponent = object : FragmentDataBindingComponent(fragment) {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }

        activityRule.activity.replaceFragment(fragment)

    }

    @Test
    fun loading() {
        viewState.value = CharacterListViewState(isLoading = true)
        onView(withId(R.id.spin_kit)).check(matches(isDisplayed()))
    }
}