package com.app.juawcevada.rickspace.data.shared.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.juawcevada.rickspace.data.character.CharacterDao
import com.app.juawcevada.rickspace.data.character.CharacterPagingInfo
import com.app.juawcevada.rickspace.model.Character


@Database(entities = [Character::class, CharacterPagingInfo::class], version = 1, exportSchema = false)
@TypeConverters(DbTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}