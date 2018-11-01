package com.app.juawcevada.rickspace.util

import okhttp3.Request
import retrofit2.Callback
import retrofit2.Response

open class MockCall<T>(val response: Response<T>) : retrofit2.Call<T> {

    override fun enqueue(callback: Callback<T>) {
        callback.onResponse(this, response)
    }

    override fun isExecuted() = true

    override fun clone() = this

    override fun isCanceled() = false

    override fun cancel() {}

    override fun execute(): Response<T> = response

    override fun request(): Request = Request.Builder().build()
}

class MockCallSuccess<T>(body: T) : MockCall<T>(Response.success(body))

class MockCallError<T>(code: Int, body: okhttp3.ResponseBody) : MockCall<T>(Response.error(code, body))