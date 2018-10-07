package com.app.juawcevada.rickspace.data.character

import com.app.juawcevada.rickspace.model.Character

data class CharacterListInfo(val info: Info, val results: List<Character>)

data class Info(val count: Int, val pages: Int, val next: String, val prev: String) {
    val nextPageInt: Int?
        get() = Regex("(?<=page=)\\d*").find(next)?.value?.run { toIntOrNull() }
}