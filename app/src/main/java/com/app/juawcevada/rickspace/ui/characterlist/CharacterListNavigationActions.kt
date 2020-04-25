package com.app.juawcevada.rickspace.ui.characterlist

sealed class CharacterListNavigationActions {
    data class OpenCharacterDetail(val id: Long) : CharacterListNavigationActions()
}