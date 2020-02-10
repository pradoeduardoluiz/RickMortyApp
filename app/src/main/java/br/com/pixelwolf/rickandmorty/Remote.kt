package br.com.pixelwolf.rickandmorty

import br.com.pixelwolf.rickandmorty.http.Character
import br.com.pixelwolf.rickandmorty.http.RetrofitFactory

object Remote {

    fun getCharacters(): List<Character> {
        val result = RetrofitFactory.getRickMortyApiAPI().getCharacters().execute()
        return result.body()?.characters ?: emptyList()
    }

}