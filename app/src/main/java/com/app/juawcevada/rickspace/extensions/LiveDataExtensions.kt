package com.app.juawcevada.rickspace.extensions

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
}