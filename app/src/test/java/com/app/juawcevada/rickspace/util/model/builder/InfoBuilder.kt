package com.app.juawcevada.rickspace.util.model.builder

import com.app.juawcevada.rickspace.data.character.Info

class InfoBuilder {
    var count: Int = 50
    var pages: Int = 5
    var next: String = "https://rickandmortyapi.com/api/character/?page=2"
    var prev: String = ""

    fun count(body: () -> Int) {
        count = body()
    }

    fun page(body: () -> Int) {
        pages = body()
    }

    fun next(body: () -> String) {
        next = body()
    }

    fun prev(body: () -> String) {
        prev = body()

    }

    fun build() = Info(count, pages, next, prev)
}

fun info(body: InfoBuilder.() -> Unit) = InfoBuilder().apply(body).build()