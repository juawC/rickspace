package com.app.juawcevada.rickspace.util.builder

@CharacterDsl
class EpisodeListBuilder {
    private val episodes = mutableListOf<String>()

    fun episode(body: () -> String) {
        episodes.add(body())
    }

    fun build() = episodes
}

fun episodes(body: EpisodeListBuilder.() -> Unit) = EpisodeListBuilder().apply(body).build()