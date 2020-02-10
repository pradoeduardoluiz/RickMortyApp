package br.com.pixelwolf.rickandmorty.model

import androidx.compose.Model
import br.com.pixelwolf.rickandmorty.http.Character

@Model
data class CharactersScreenState(
    var selectedTab: Int,
    var isDeleteDialogOpen: Boolean,
    var characterToDelete: Character?,
    var charactersFavorites: MutableSet<Character> = mutableSetOf()
)