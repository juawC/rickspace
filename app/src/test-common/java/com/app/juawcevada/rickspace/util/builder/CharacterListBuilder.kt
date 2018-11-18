package com.app.juawcevada.rickspace.util.builder

class CharacterListBuilder {
    private val listItems = mutableListOf<com.app.juawcevada.rickspace.model.Character>()

    fun character(body: CharacterBuilder.() -> Unit) {
        listItems.add(CharacterBuilder().apply(body).build())
    }

    fun build() = listItems
}

fun characterList(body: CharacterListBuilder.() -> Unit) =
        CharacterListBuilder().apply(body).build()