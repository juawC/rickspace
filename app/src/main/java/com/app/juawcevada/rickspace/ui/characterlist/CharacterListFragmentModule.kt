package com.app.juawcevada.rickspace.ui.characterlist

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module

@Module
abstract class CharacterListFragmentModule {

    @Binds
    internal abstract fun bindFragment(fragment: CharacterListFragment): Fragment
}