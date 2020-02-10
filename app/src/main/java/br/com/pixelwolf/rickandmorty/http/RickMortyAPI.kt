package br.com.pixelwolf.rickandmorty.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RickMortyAPI {

    // GET
    @GET("character")
    fun getCharacters(
    ): Call<CharacterResponse>

}