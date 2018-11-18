package com.app.juawcevada.rickspace.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
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
)