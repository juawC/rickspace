package com.app.juawcevada.rickspace.ui.characterlist

import androidx.paging.PagedList
import com.app.juawcevada.rickspace.model.Character

data class CharacterListViewState(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val charactersList: PagedList<Character>? = null,
        val errorMessage: String? = null
)