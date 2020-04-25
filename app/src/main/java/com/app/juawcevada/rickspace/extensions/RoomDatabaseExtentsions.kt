package com.app.juawcevada.rickspace.extensions

import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun RoomDatabase.runInTransactionSuspended(
    dispatcher: CoroutineDispatcher, body: () -> Unit
) = withContext(dispatcher) {
    runInTransaction(body)
}