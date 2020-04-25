package com.app.juawcevada.rickspace.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass


@Entity
@JsonClass(generateAdapter = true)
data class Character(
    @PrimaryKey
    val id: Long,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    @Embedded val origin: Origin,
    @Embedded val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String
) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1

    // This shouldn't be here, it belongs to another db table, but putting it here makes this
    // implementation easier
    var nextPage: Int = -1
}