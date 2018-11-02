package com.app.juawcevada.rickspace.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Gets the value of a LiveData safely.
 */
@Throws(InterruptedException::class)
fun <T> LiveData<T>.getValueTest(): T? {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getValueTest.removeObserver(this)
        }
    }
    this.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)
    return data
}

/**
 * Gets the value of a LiveData safely.
 */
@Throws(InterruptedException::class)
fun <T> LiveData<T>.observeTest() {
    val observer = Observer<T> { }
    this.observeForever(observer)
}