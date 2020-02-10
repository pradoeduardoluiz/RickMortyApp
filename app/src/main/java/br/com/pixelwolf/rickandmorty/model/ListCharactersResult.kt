package br.com.pixelwolf.rickandmorty.model

import androidx.compose.Model
import br.com.pixelwolf.rickandmorty.http.Character

@Model
data class ListCharactersResult(
    var loading: Boolean,
    var characters: List<Character>
)