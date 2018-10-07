package com.app.juawcevada.rickspace.model

import androidx.room.ColumnInfo

data class Origin (@ColumnInfo(name = "origin_name") val name: String, @ColumnInfo(name = "origin_url") val url: String)