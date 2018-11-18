package com.app.juawcevada.rickspace.ui.characterlist

import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.shared.LceViewState

data class CharacterListViewState(
        override val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val charactersList: List<Character>? = null,
        override val errorMessage: String? = null
) : LceViewState