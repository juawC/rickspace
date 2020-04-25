package com.app.juawcevada.rickspace.data.shared.repository

sealed class Resource<out Data>(open val data: Data?, open val error: Throwable?) {

    inline fun <NewType> map(f: (Data) -> NewType): Resource<NewType> =
            when (this) {
                is ResourceError -> ResourceError(data?.let(f), error)
                is ResourceSuccess -> ResourceSuccess(data?.let(f))
                is ResourceLoading -> ResourceLoading(data?.let(f))
            }
}

data class ResourceError<out Data>(
    override val data: Data? = null,
    override val error: Throwable? = null
) : Resource<Data>(data, error)

data class ResourceSuccess<out Data>(
    override val data: Data? = null
) : Resource<Data>(data, null)

data class ResourceLoading<out Data>(
    override val data: Data? = null
) : Resource<Data>(data, null)



