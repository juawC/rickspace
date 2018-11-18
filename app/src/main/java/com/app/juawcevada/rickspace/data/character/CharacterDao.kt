package com.app.juawcevada.rickspace.data.character


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.juawcevada.rickspace.model.Character

@Dao
interface CharacterDao {

    @Query("DELETE FROM character")
    fun deleteAllCharacters()

    @Query("SELECT * FROM character")
    fun getAllCharacters(): LiveData<List<Character>>

    @Query("SELECT * FROM character WHERE id =:id")
    fun getCharacterById(id: Long): LiveData<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(characters: List<Character>)
}