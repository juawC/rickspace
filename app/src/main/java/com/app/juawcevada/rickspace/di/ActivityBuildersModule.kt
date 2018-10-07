package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.ui.MainActivity
import com.app.juawcevada.rickspace.ui.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity
}