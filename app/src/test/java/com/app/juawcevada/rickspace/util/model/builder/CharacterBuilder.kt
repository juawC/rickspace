package com.app.juawcevada.rickspace.util.model.builder

import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.model.Location
import com.app.juawcevada.rickspace.model.Origin

@CharacterDsl class CharacterBuilder {
    private var id: Long = 0
    private var name: String = "x"
    private var status: String = "schrodingered"
    private var species: String = "unknown monster"
    private var type: String = "Unknown"
    private var gender: String = "maybe both"
    private var origin: Origin = Origin("Ask marvel", "http://sql_url")
    private var location: Location = Location("Ask google", "http://they_re_watching")
    private var image: String = "http://boring_url_image"
    private var episode: List<String> = listOf("1", "2")
    private var url: String = "http://boring_url"
    private var nextPage: Int = -1


    fun id(body: () -> Long) {
        id = body()
    }

    fun name(body: () -> String) {
        name = body()
    }

    fun status(body: () -> String) {
        status = body()
    }

    fun species(body: () -> String) {
        species = body()
    }

    fun type(body: () -> String) {
        type = body()
    }

    fun gender(body: () -> String) {
        gender = body()
    }

    fun origin(body: OriginBuilder.() -> Unit) {
        origin = OriginBuilder().apply(body).build()
    }

    fun location(body: LocationBuilder.() -> Unit) {
        location = LocationBuilder().apply(body).build()
    }

    fun image(body: () -> String) {
        image = body()
    }

    fun episodes(body: EpisodeListBuilder.() -> Unit) {
        episode = EpisodeListBuilder().apply(body).build()
    }

    fun url(body: () -> String) {
        url = body()
    }

    fun nextPage(body: () -> Int) {
        nextPage = body()
    }

    fun build() =
            Character(
                    id,
                    name,
                    status,
                    species,
                    type,
                    gender,
                    origin,
                    location,
                    image,
                    episode,
                    url)
                    .apply { nextPage = this@CharacterBuilder.nextPage }
}

fun character(body: CharacterBuilder.() -> Unit) = CharacterBuilder().apply(body).build()