package com.app.juawcevada.rickspace.ui.charaterdetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

import com.app.juawcevada.rickspace.databinding.CharacterDetailFragmentBinding
import com.app.juawcevada.rickspace.extensions.lazyViewModelProvider
import com.app.juawcevada.rickspace.ui.shared.FragmentDataBindingComponent
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CharacterDetailFragment : Fragment() {

    @Inject
    lateinit var fragmentDataBindingComponent: FragmentDataBindingComponent

    @Inject
    lateinit var viewModelFactory: CharacterDetailViewModel.Factory

    private val navigationArguments: CharacterDetailFragmentArgs by navArgs()

    private val characterViewModel by lazyViewModelProvider {
        viewModelFactory.create(navigationArguments.characterId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {

        val binding =
                CharacterDetailFragmentBinding
                        .inflate(
                                inflater,
                                container,
                                false,
                                fragmentDataBindingComponent).apply {
                            lifecycleOwner = this@CharacterDetailFragment
                            episodesList.adapter = CharacterEpisodesAdapter()
                            viewModel = characterViewModel
                        }

        return binding.root
    }
}
