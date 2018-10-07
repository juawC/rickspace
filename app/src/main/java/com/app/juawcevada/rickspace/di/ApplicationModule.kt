package com.app.juawcevada.rickspace.di

import android.content.Context
import com.app.juawcevada.rickspace.Application
import dagger.Binds
import dagger.Module

@Module
abstract class ApplicationModule {

    @Binds
    internal abstract fun provideContext(application: Application): Context
}