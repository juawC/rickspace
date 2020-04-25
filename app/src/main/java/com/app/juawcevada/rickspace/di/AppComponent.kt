package com.app.juawcevada.rickspace.di

import android.content.Context
import com.app.juawcevada.rickspace.RickApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApiModule::class,
    ConfigsModule::class,
    DBModule::class,
    RepositoriesModule::class,
    ViewModelModule::class,
    ActivityBuildersModule::class
])
interface AppComponent : AndroidInjector<RickApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }
}