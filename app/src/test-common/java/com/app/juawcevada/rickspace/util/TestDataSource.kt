package com.app.juawcevada.rickspace.util

import androidx.paging.PositionalDataSource
import com.app.juawcevada.rickspace.model.Character

class TestDataSource(
    private val charList: List<Character> = emptyList()
) : PositionalDataSource<Character>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Character>) {
        callback.onResult(charList)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Character>) {
        callback.onResult(charList, 0, charList.size)
    }
}