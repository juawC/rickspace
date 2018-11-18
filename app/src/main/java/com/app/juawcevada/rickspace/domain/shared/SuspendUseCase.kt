package com.app.juawcevada.rickspace.domain.shared


abstract class SuspendUseCase<in P, R> {

    suspend operator fun invoke(parameters: P): R = execute(parameters)

    protected abstract suspend fun execute(parameters: P): R
}

