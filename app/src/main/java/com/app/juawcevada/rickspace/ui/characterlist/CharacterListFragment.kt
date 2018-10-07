package com.app.juawcevada.rickspace.ui.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.databinding.CharacterListFragmentBinding
import com.app.juawcevada.rickspace.event.EventObserver
import com.app.juawcevada.rickspace.extensions.checkExhaustion
import com.app.juawcevada.rickspace.extensions.setUpSnackbar
import com.app.juawcevada.rickspace.extensions.viewModelProvider
import com.app.juawcevada.rickspace.ui.shared.FragmentDataBindingComponent
import com.app.juawcevada.rickspace.ui.shared.VerticalSpaceItemDecoration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CharacterListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentDataBindingComponent: FragmentDataBindingComponent

    private lateinit var viewModel: CharacterListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)

        viewModel = viewModelProvider(viewModelFactory)

        val binding: CharacterListFragmentBinding =
                CharacterListFragmentBinding.inflate(inflater, container, false).also {
                    it.setLifecycleOwner(this)
                    it.viewModel = viewModel
                    it.actions = viewModel
                    it.list.apply {
                        adapter = CharacterListAdapter(fragmentDataBindingComponent) { character ->
                            viewModel.openCharacter(character.id)
                        }
                        addItemDecoration(
                                VerticalSpaceItemDecoration(
                                        this.context,
                                        R.dimen.character_list_divider_height,
                                        true,
                                        true))
                    }
                }

        viewModel.navigationAction.observe(this, EventObserver { event ->

            when (event) {
                is CharacterListNavigationActions.OpenCharacterDetail -> {
                    val action =
                            CharacterListFragmentDirections
                                    .actionCharacterListFragmentToCharacterDetailFragment(event.id)
                    findNavController().navigate(action)
                }
            }.checkExhaustion
        })

        setUpSnackbar(viewModel.errorMessage, binding.root)

        return binding.root
    }
}
