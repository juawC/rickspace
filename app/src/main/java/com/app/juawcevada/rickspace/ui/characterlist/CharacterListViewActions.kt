package com.app.juawcevada.rickspace.ui.characterlist

import com.app.juawcevada.rickspace.ui.shared.LceViewActions

interface CharacterListViewActions : LceViewActions {
    fun openCharacter(id: Long)
    fun refresh()
}