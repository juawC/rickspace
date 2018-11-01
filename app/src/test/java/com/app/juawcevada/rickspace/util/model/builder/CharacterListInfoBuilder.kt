package com.app.juawcevada.rickspace.util.model.builder

import com.app.juawcevada.rickspace.data.character.CharacterListInfo
import com.app.juawcevada.rickspace.data.character.Info
import com.app.juawcevada.rickspace.model.Character

class CharacterListInfoBuilder {
    var info: Info = InfoBuilder().build()
    var results: List<Character> = emptyList()

    fun info(body: ()-> Info) {
        info = body()
    }

    fun results(body: CharacterListBuilder.()-> Unit) {
        results = CharacterListBuilder().apply(body).build()
    }

    fun build() = CharacterListInfo(info, results)
}

fun characterListInfo(body: CharacterListInfoBuilder.() -> Unit) =
        CharacterListInfoBuilder().apply(body).build()