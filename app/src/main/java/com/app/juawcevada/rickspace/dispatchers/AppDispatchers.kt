package com.app.juawcevada.rickspace.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDispatchers(
        val Main: CoroutineDispatcher,
        val IO: CoroutineDispatcher,
        val Default: CoroutineDispatcher
) {

    @Inject
    constructor() : this(
            Dispatchers.Main,
            Dispatchers.IO,
            Dispatchers.Default
    )
}