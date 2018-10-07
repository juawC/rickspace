package com.app.juawcevada.rickspace.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.juawcevada.rickspace.di.annotation.ViewModelKey
import com.app.juawcevada.rickspace.ui.ViewModelFactory
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListViewModel
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Provides the view model factory with view models maps and binds the ViewModelFactory type to
 * a ViewModelProvider.Factory type.
 */
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CharacterDetailViewModel::class)
    internal abstract fun bindCharacterDetailsViewModel(userViewModel: CharacterDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CharacterListViewModel::class)
    internal abstract fun bindCharacterListViewModel(userViewModel: CharacterListViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}