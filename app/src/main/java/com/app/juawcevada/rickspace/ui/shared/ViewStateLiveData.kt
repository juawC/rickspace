package com.app.juawcevada.rickspace.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * MediatorLiveData with helper functions to help update a ViewState
 */
class ViewStateLiveData<T>(initialState: T) : MediatorLiveData<T>() {

    init {
        value = initialState
    }

    val currentState: T
        get() = value!!

    fun <S> addNewStateSource(source: LiveData<S>, body: T.(S) -> T) {
        this.addSource(source) {
            dispatchState { body(it) }
        }
    }

    fun dispatchState(newStateCreator: T.() -> T) {
        value = value!!.newStateCreator()
    }
}