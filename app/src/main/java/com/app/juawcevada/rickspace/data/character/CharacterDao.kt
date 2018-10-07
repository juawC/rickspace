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

    @Query("SELECT * FROM character ORDER BY indexInResponse ASC")
    fun getAllCharacters(): DataSource.Factory<Int, Character>

    @Query("SELECT * FROM character WHERE id =:id")
    fun getCharacterById(id: Long): LiveData<Character>

    @Query("SELECT MAX(indexInResponse) + 1 FROM character")
    fun getNextIndexCharacter() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(characters: List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPageInfo(pageInfo: CharacterPagingInfo)
}