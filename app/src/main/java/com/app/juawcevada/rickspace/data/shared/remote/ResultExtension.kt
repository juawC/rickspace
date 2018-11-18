package com.app.juawcevada.rickspace.data.shared.remote


import com.app.juawcevada.rickspace.data.shared.repository.Resource
import com.app.juawcevada.rickspace.data.shared.repository.ResourceError
import com.app.juawcevada.rickspace.data.shared.repository.ResourceSuccess
import ru.gildor.coroutines.retrofit.Result

fun <T : Any> Result<T>.doOnSuccess(onSuccess: (T) -> Unit = {}) {
    when (this) {
        is Result.Ok -> {
            try {
                onSuccess(value)
            } catch (exception: Exception) {
                ResourceError<T>(error = exception)
            }
        }
    }
}

fun <T : Any> Result<T>.toResource(): Resource<T> {
    return when (this) {
        is Result.Ok -> {
            try {
                ResourceSuccess(value)
            } catch (exception: Exception) {
                ResourceError<T>(error = exception)
            }
        }
        is Result.Error -> {
            ResourceError(error = exception)
        }
        is Result.Exception -> {
            ResourceError(error = exception)
        }
    }
}