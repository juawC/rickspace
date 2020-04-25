package com.app.juawcevada.rickspace.ui.characterlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.databinding.CharacterListFragmentBinding
import com.app.juawcevada.rickspace.event.EventObserver
import com.app.juawcevada.rickspace.extensions.checkExhaustion
import com.app.juawcevada.rickspace.extensions.lazyViewModelProvider
import com.app.juawcevada.rickspace.extensions.setUpSnackbar
import com.app.juawcevada.rickspace.extensions.viewModelProvider
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import com.app.juawcevada.rickspace.ui.shared.FragmentDataBindingComponent
import com.app.juawcevada.rickspace.ui.shared.VerticalSpaceItemDecoration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

@OpenClassOnDebug
class CharacterListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: dagger.Lazy<CharacterListViewModel>

    @Inject
    lateinit var fragmentDataBindingComponent: FragmentDataBindingComponent

    private val characterListViewModel: CharacterListViewModel by lazyViewModelProvider {
        viewModelFactory.get()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: CharacterListFragmentBinding =
                CharacterListFragmentBinding.inflate(inflater, container, false).apply {
                    lifecycleOwner = this@CharacterListFragment
                    viewModel = characterListViewModel
                    viewActions = characterListViewModel
                    list.setupListAdapter()
                }

        characterListViewModel.navigationAction.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                is CharacterListNavigationActions.OpenCharacterDetail -> navigateToDetail(event)
            }.checkExhaustion
        })

        setUpSnackbar(characterListViewModel.errorMessage, binding.root)

        return binding.root
    }

    private fun RecyclerView.setupListAdapter() {
        adapter = CharacterListAdapter(fragmentDataBindingComponent, characterListViewModel::openCharacter.id)
        addItemDecoration(
                VerticalSpaceItemDecoration(
                        context,
                        R.dimen.character_list_divider_height,
                        includeTop = true,
                        includeBottom = true)
        )
    }

    private fun navigateToDetail(event: CharacterListNavigationActions.OpenCharacterDetail) {
        val action =
                CharacterListFragmentDirections
                        .actionCharacterListFragmentToCharacterDetailFragment(event.id)
        navController().navigate(action)
    }

    val ((Long) -> Unit).id: ((Character) -> Unit)
        get() = { character -> this(character.id) }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
