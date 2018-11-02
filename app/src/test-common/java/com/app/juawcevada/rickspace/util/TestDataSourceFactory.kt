package com.app.juawcevada.rickspace.util

import androidx.paging.DataSource
import com.app.juawcevada.rickspace.model.Character

class TestDataSourceFactory(
        var charList: MutableList<Character> = mutableListOf()
) : DataSource.Factory<Int, Character>() {

    override fun create() = TestDataSource(charList)
}