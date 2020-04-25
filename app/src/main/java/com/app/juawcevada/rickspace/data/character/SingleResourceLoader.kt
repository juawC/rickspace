package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class SingleResourceLoader {

    private var lastAction: () -> Flow<Resource<Unit>> = { flow<Resource<Unit>> { emit(ResourceSuccess()) } }
    val currentState = MediatorLiveData<Resource<Unit>>()

    fun loadData(action: () -> Flow<Resource<Unit>>) {
        // Avoid concurrent network calls
        if (currentState.value is ResourceLoading)
            return

        lastAction = action

        val actionLiveData: LiveData<Resource<Unit>> = liveData { action().collect(::emit) }
        currentState.addSource(actionLiveData) { newResource ->
            currentState.value = newResource
            if (newResource !is ResourceLoading) currentState.removeSource(actionLiveData)
        }
    }

    fun retryLastAction() {
        loadData(lastAction)
    }
}