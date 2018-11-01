package com.app.juawcevada.rickspace.util.model.builder

@CharacterDsl class EpisodeListBuilder {
    private val episodes = mutableListOf<String>()

    fun episode(body: () -> String) {
        episodes.add(body())
    }

    fun build() = episodes
}