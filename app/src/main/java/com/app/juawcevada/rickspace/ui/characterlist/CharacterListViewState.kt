package com.app.juawcevada.rickspace.ui.characterlist

import androidx.paging.PagedList
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.ui.shared.LceViewState

data class CharacterListViewState(
        override val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val charactersList: PagedList<Character>? = null,
        override val errorMessage: String? = null
) : LceViewState