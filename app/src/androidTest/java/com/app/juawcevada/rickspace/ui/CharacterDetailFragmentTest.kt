package com.app.juawcevada.rickspace.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.juawcevada.rickspace.testing.SingleFragmentActivity
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailFragment
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailViewModel
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailViewState
import com.app.juawcevada.rickspace.util.builder.character
import com.app.juawcevada.rickspace.util.builder.episodes
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.app.juawcevada.rickspace.R.id.*
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailFragmentArgs
import com.app.juawcevada.rickspace.ui.shared.FragmentBindingAdapters
import com.app.juawcevada.rickspace.ui.shared.FragmentDataBindingComponent
import com.app.juawcevada.rickspace.util.checkThatMatches
import com.app.juawcevada.rickspace.util.createTestFactory
import com.app.juawcevada.rickspace.util.onRecyclerViewPosition
import com.app.juawcevada.rickspace.R
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDetailFragmentTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CharacterDetailViewModel
    private lateinit var viewState: MutableLiveData<CharacterDetailViewState>
    private lateinit var fragment: CharacterDetailFragment

    @Before
    fun setUp() {
        val activityScenario = ActivityScenario.launch(SingleFragmentActivity::class.java)
        viewState = MutableLiveData()
        fragment = CharacterDetailFragment().apply {
            arguments = CharacterDetailFragmentArgs.Builder(1).build().toBundle()
        }

        viewModel = mock {
            on { viewState } doReturn this@CharacterDetailFragmentTest.viewState
        }

        fragment.viewModelFactory = viewModel.createTestFactory()

        fragment.fragmentDataBindingComponent = object : FragmentDataBindingComponent(fragment) {
            override fun getFragmentBindingAdapters() = mock<FragmentBindingAdapters>()
        }

        activityScenario.onActivity {
            it.replaceFragment(fragment)
        }
    }

    @Test
    fun showDetails() {
        viewState.value =
                CharacterDetailViewState(
                        character = character {
                            name { "Rick" }
                            location {
                                name { "Earth" }
                            }
                            origin {
                                name { "Another Earth" }
                            }
                            species { "Humanish" }
                        },
                        characterEpisodes = episodes {
                            episode { "1" }
                            episode { "2" }
                            episode { "3" }
                        }
                )
        charName checkThatMatches withText("Rick")
        charOrigin checkThatMatches withText("Another Earth")
        charLocation checkThatMatches withText("Earth")
        charSpecies checkThatMatches withText("Humanish")

        episodesList onRecyclerViewPosition 0 checkThatMatches withText(getEpisodeString(1))
        episodesList onRecyclerViewPosition 1 checkThatMatches withText(getEpisodeString(2))
        episodesList onRecyclerViewPosition 2 checkThatMatches withText(getEpisodeString(3))
    }

    private fun getEpisodeString(episodeNumber: Long) =
            getApplicationContext<Application>().getString(R.string.episode_number, episodeNumber)
}