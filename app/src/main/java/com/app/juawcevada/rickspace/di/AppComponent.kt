package com.app.juawcevada.rickspace.di

import com.app.juawcevada.rickspace.Application
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class,
    ApiModule::class,
    ConfigsModule::class,
    DBModule::class,
    RepositoriesModule::class,
    ViewModelModule::class,
    ActivityBuildersModule::class
])
interface AppComponent : AndroidInjector<Application> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<Application>()
}