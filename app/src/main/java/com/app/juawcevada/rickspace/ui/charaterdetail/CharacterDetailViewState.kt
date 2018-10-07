package com.app.juawcevada.rickspace.ui.charaterdetail

import com.app.juawcevada.rickspace.model.Character

data class CharacterDetailViewState(
        val isRefreshing: Boolean = false,
        val character: Character? = null,
        val characterEpisodes: List<String> = emptyList(),
        val errorMessage: String? = null
)