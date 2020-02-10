package br.com.pixelwolf.rickandmorty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import br.com.pixelwolf.rickandmorty.model.ListCharactersResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val charactersResult: ListCharactersResult = ListCharactersResult(false, emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RickMortyScreen(listCharactersResult = charactersResult)
            }
        }

        lifecycleScope.launch {
            charactersResult.loading = true
            val books = withContext(Dispatchers.IO) {
                Remote.getCharacters()
            }
            charactersResult.characters = books ?: emptyList()
            charactersResult.loading = false
        }
    }
}

@Composable
fun RickMortyScreen(listCharactersResult: ListCharactersResult) {
    VerticalScroller {
        Column(modifier = Spacing(16.dp), crossAxisAlignment = CrossAxisAlignment.Center) {
            listCharactersResult.characters.forEach {
                Row(
                    modifier = Spacing(top = 32.dp),
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceEvenly
                ) {
                    Text(text = it.name)
                }
            }
        }
    }
}
