package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.ui.MainActivity
import com.app.juawcevada.rickspace.ui.MainActivityModule
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@AssistedModule
@Module(includes = [AssistedInject_ActivityBuildersModule::class])
internal abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity
}