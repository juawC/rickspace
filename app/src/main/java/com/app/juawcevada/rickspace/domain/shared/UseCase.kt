package com.app.juawcevada.rickspace.domain.shared
import kotlinx.coroutines.CoroutineScope


abstract class UseCase<in P, R> {

    operator fun invoke(parameters: P): CoroutineScope.()-> R = {execute(this, parameters)}

    protected abstract fun execute(coroutineScope: CoroutineScope, parameters: P): R
}

fun <R> CoroutineScope.runInScope(body: ()-> (CoroutineScope.()-> R)): R = body()()
