package com.app.juawcevada.rickspace.data.shared.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.app.juawcevada.rickspace.dispatchers.AppDispatchers
import com.app.juawcevada.rickspace.extensions.checkExhaustion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult

fun <RemoteDataType : Any, LocalDataType : Any> createLiveDataDataSource(
        coroutineScope: CoroutineScope,
        appDispatchers: AppDispatchers,
        remoteFetchCall: () -> Call<RemoteDataType>,
        localFetchCall: () -> LiveData<LocalDataType>,
        localSaveCall: (RemoteDataType) -> Unit,
        isNewDataRequired: (LocalDataType?) -> Boolean = { true }
): LiveData<Resource<LocalDataType>> {

    val localData = localFetchCall()

    return MediatorLiveData<Resource<LocalDataType>>().apply {
        addSource(localData) {
            removeSource(localData)
            coroutineScope.launch(appDispatchers.IO) {
                if (it != null && !isNewDataRequired(it)) {
                    addSource<LocalDataType>(localData) { data ->
                        value = ResourceSuccess(data) }
                } else {
                    postValue(ResourceLoading(it))
                    val remoteResult = remoteFetchCall().awaitResult()

                    when (remoteResult) {
                        is Result.Error ->
                            postValue(ResourceError(it, remoteResult.exception))
                        is Result.Exception ->
                            postValue(ResourceError(it, remoteResult.exception))
                        is Result.Ok -> {
                            localSaveCall(remoteResult.value)
                            withContext(appDispatchers.Main) {
                                addSource<LocalDataType>(localFetchCall()) { data ->
                                    value = ResourceSuccess(data) }
                            }
                        }
                    }.checkExhaustion

                }
            }
        }
    }
}