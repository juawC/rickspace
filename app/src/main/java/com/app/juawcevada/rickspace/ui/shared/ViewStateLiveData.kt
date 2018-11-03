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

    fun <S> addNewStateSource(source: LiveData<S>, newStateCreator: T.(S) -> T) {
        this.addSource(source) {
            dispatchState { newStateCreator(it) }
        }
    }

    private fun dispatchState(newStateCreator: T.() -> T) {
        value = value!!.newStateCreator()
    }
}