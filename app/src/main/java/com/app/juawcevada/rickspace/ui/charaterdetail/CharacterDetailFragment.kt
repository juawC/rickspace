package com.app.juawcevada.rickspace.ui.charaterdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.databinding.CharacterDetailFragmentBinding
import com.app.juawcevada.rickspace.extensions.viewModelProvider
import com.app.juawcevada.rickspace.ui.FragmentDataBindingComponent
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CharacterDetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentDataBindingComponent: FragmentDataBindingComponent

    private lateinit var viewModel: CharacterDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        AndroidSupportInjection.inject(this)

        val characterId = CharacterDetailFragmentArgs.fromBundle(arguments).characterId

        viewModel = viewModelProvider(viewModelFactory)
        viewModel.setCharacterId(characterId)

        val binding =
                CharacterDetailFragmentBinding
                        .inflate(
                                inflater,
                                container,
                                false,
                                fragmentDataBindingComponent).also {
                            it.setLifecycleOwner(this)
                            it.episodesList.adapter = CharacterEpisodesAdapter()
                            it.viewModel = viewModel
                        }

        return binding.root
    }

}
