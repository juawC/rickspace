package com.app.juawcevada.rickspace.ui.charaterdetail

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module

@Module
abstract class CharacterDetailFragmentModule {

    @Binds
    internal abstract fun bindFragment(fragment: CharacterDetailFragment): Fragment
}