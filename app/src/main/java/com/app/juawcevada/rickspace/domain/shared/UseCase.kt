package com.app.juawcevada.rickspace.domain.shared

import kotlinx.coroutines.experimental.Job

abstract class UseCase<in P, R> {

    protected var parentJob: Job = Job()

    operator fun invoke(parameters: P): R {
        return execute(parameters)
    }

    protected abstract fun execute(parameters: P): R

    fun cancel() {
        parentJob.cancel()
    }

    fun isRunning() = parentJob.isActive
}

operator fun <R> UseCase<Unit, R>.invoke(): R = this(Unit)