package com.app.juawcevada.rickspace.util.model.builder

import com.app.juawcevada.rickspace.model.Location

@CharacterDsl
class LocationBuilder {
    private var name = "Ask google"
    private var url = "http://they_re_watching"

    fun name(body: () -> String) {
        name = body()
    }

    fun url(body: () -> String) {
        url = body()
    }

    fun build() = Location(name, url)
}

fun location(body: LocationBuilder.() -> Unit) = LocationBuilder().apply(body).build()