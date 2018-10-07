package com.app.juawcevada.rickspace.data.shared.remote

import com.app.juawcevada.rickspace.data.character.CharacterListInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import com.app.juawcevada.rickspace.model.Character
import retrofit2.http.Query


interface RickAndMortyService {
    @GET("character/")
    fun getCharacters(): Call<CharacterListInfo>

    @GET("character")
    fun getCharactersByPage(@Query("page") page: Int): Call<CharacterListInfo>

    @GET("character/{id}")
    fun getCharacter(@Path("id") id: Int): Call<Character>
}