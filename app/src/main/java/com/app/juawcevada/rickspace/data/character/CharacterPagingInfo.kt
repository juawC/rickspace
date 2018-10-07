package com.app.juawcevada.rickspace.data.character

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CharacterPagingInfo(val nextPage: Int, @PrimaryKey val id: Int = 0)

