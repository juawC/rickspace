package com.app.juawcevada.rickspace.util

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.app.juawcevada.rickspace.model.Character
import java.util.concurrent.Executor

class TestDataSourceFactory(
    private var charList: MutableList<Character> = mutableListOf()
) : DataSource.Factory<Int, Character>() {

    override fun create() = TestDataSource(charList)

    fun buildPagedList(): PagedList<Character> {
        val instantExecutor = Executor { it.run() }
        val pagingConfig = PagedList.Config.Builder().setPageSize(1).build()

        return PagedList
                .Builder(this.create(), pagingConfig)
                .setNotifyExecutor(instantExecutor)
                .setFetchExecutor(instantExecutor)
                .build()
    }
}

fun MutableList<Character>.toPagedList(): PagedList<Character> = TestDataSourceFactory(this).buildPagedList()