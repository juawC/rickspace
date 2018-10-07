package com.app.juawcevada.rickspace.data.shared.local

import androidx.room.TypeConverter

class DbTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString()

    @TypeConverter
    fun toStringList(list: String?): List<String>? = list?.split(",")?.map { it.trim() }
}