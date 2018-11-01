package com.app.juawcevada.rickspace.data.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceLoading
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess

class SingleResourceLoader {

    private val currentLoadAction: MediatorLiveData<LoadAction> = MediatorLiveData()

    val currentState: LiveData<Resource<Unit>> by lazy {
        Transformations.map(currentLoadAction) { it.state }
    }

    fun loadData(action: () -> LiveData<Resource<Unit>>) {
        // Avoid concurrent network calls
        if (currentLoadAction.value?.state is ResourceLoading) {
            return
        } else {
            currentLoadAction.value = LoadAction(action = action)
        }

        action().let { responseState ->
            currentLoadAction.value = LoadAction(ResourceLoading(), action)
            currentLoadAction.addSource(responseState) {
                currentLoadAction.value = LoadAction(it, action)

                if (it is ResourceSuccess || it is ResourceError) {
                    currentLoadAction.removeSource(responseState)
                }
            }
        }
    }

    fun retryLastAction() {
        currentLoadAction.value?.let {
            if (it.state is ResourceError) {
                loadData (it.action)
            }
        }

    }

    private data class LoadAction(
            val state: Resource<Unit> = ResourceLoading(),
            val action: () -> LiveData<Resource<Unit>>
    )
}