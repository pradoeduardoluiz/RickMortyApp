package br.com.pixelwolf.rickandmorty.http

import android.text.style.CharacterStyle
import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("info")
    val info: Info,
    @SerializedName("results")
    val characters: List<Character>
)