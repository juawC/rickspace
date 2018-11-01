package com.app.juawcevada.rickspace.util.model.builder

import com.app.juawcevada.rickspace.model.Origin

@CharacterDsl
class OriginBuilder {
    private var name = "Ask marvel"
    private var url = "http://sql_url"

    fun name(body: () -> String) {
        name = body()
    }

    fun url(body: () -> String) {
        url = body()
    }

    fun build() = Origin(name, url)
}

fun origin(body: OriginBuilder.() -> Unit) = OriginBuilder().apply(body).build()