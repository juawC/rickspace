package com.app.juawcevada.rickspace.data.shared.repository

sealed class Resource<out Data>(val data: Data?, val error: Throwable?) {

    inline fun <NewType> map(f: (Data) -> NewType): Resource<NewType> =
            when (this) {
                is ResourceError -> ResourceError(data?.let(f), error)
                is ResourceSuccess -> ResourceSuccess(data?.let(f))
                is ResourceLoading -> ResourceLoading(data?.let(f))
            }
}

class ResourceError<out Data>(
        data: Data? = null,
        error: Throwable? = null) : Resource<Data>(data, error)

class ResourceSuccess<out Data>(data: Data? = null) : Resource<Data>(data, null)

class ResourceLoading<out Data>(data: Data? = null) : Resource<Data>(data, null)



