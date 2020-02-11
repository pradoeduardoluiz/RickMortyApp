package br.com.pixelwolf.rickandmorty.model

import androidx.compose.Model
import br.com.pixelwolf.rickandmorty.http.Character
import br.com.pixelwolf.rickandmorty.http.Remote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Model
data class ListCharactersResult(
    var loading: Boolean,
    var characters: List<Character>
) {
    suspend fun fetchCharacters() {
        loading = true
        val listOfCharacters = withContext(Dispatchers.IO) {
            Remote.getCharacters()
        }
        characters = listOfCharacters
        loading = false
    }
}