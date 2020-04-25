package com.app.juawcevada.rickspace.domain.shared

abstract class UseCase<in P, R> {

    operator fun invoke(parameters: P): R = execute(parameters)
    protected abstract fun execute(parameters: P): R
}

operator fun <T> UseCase<Unit, T>.invoke() = invoke(Unit)