package com.app.juawcevada.rickspace.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock

/**
 * Gets the value of a LiveData safely.
 */
fun <T> LiveData<T>.observeTest() : Observer<T> {
    val observer = mock<Observer<T>>()
    this.observeForever(observer)
    return observer
}