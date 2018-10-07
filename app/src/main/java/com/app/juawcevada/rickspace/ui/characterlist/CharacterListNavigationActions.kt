package com.app.juawcevada.rickspace.ui.characterlist

sealed class CharacterListNavigationActions {
    class OpenCharacterDetail(val id: Long) : CharacterListNavigationActions()
}