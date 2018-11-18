package com.app.juawcevada.rickspace.util

import com.nhaarman.mockitokotlin2.KStubbing
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.mockito.stubbing.OngoingStubbing

fun <T : Any, R> KStubbing<T>.onBlocking(
        m: suspend T.() -> R
): OngoingStubbing<R> {
    return runBlocking { whenever(mock.m()) }
}

fun <T> T.addStubs(stubbing: KStubbing<T>.(T) -> Unit) = KStubbing(this).stubbing(this)

fun <T : Any, R> T.wheneverBlocking(
        m: suspend T.() -> R
): OngoingStubbing<R> {
    return runBlocking { whenever(m()) }
}