package com.app.juawcevada.rickspace.util.model.builder

class CharacterListBuilder {
    private val listItems = mutableListOf<com.app.juawcevada.rickspace.model.Character>()

    fun episode(body: () -> com.app.juawcevada.rickspace.model.Character) {
        listItems.add(body())
    }

    fun build() = listItems
}