package com.app.juawcevada.rickspace.ui

import com.app.juawcevada.rickspace.ui.characterlist.CharacterListFragment
import com.app.juawcevada.rickspace.ui.characterlist.CharacterListFragmentModule
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailFragment
import com.app.juawcevada.rickspace.ui.charaterdetail.CharacterDetailFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [CharacterDetailFragmentModule::class])
    internal abstract fun contributeCharacterDetailFragment(): CharacterDetailFragment

    @ContributesAndroidInjector(modules = [CharacterListFragmentModule::class])
    internal abstract fun contributeCharacterListFragment(): CharacterListFragment
}