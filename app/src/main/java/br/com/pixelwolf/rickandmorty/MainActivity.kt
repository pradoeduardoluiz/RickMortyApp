package br.com.pixelwolf.rickandmorty

import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.*
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.res.vectorResource
import br.com.pixelwolf.rickandmorty.http.Character
import br.com.pixelwolf.rickandmorty.http.Remote
import br.com.pixelwolf.rickandmorty.model.CharactersScreenState
import br.com.pixelwolf.rickandmorty.model.ListCharactersResult
import br.com.pixelwolf.rickandmorty.util.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val charactersResult: ListCharactersResult = ListCharactersResult(false, emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colors = MaterialColors(primary = Color.Green)) {
                RickMortyScreen(result = charactersResult)
            }
        }

        lifecycleScope.launch {
            charactersResult.loading = true
            val characters = withContext(Dispatchers.IO) {
                Remote.getCharacters()
            }
            charactersResult.characters = characters ?: emptyList()
            charactersResult.loading = false
        }
    }
}

@Composable
fun RickMortyScreen(result: ListCharactersResult) {

    val context = +ambient(ContextAmbient)
    val resources = context.resources
    if (result.loading) {
        Loading(resources)
        return
    }

    val screenState by +state {
        CharactersScreenState(0, false, null)
    }

    if (screenState.isDeleteDialogOpen) {
        screenState.characterToDelete?.let { character ->
            DeleteFavCharacterDialog(
                resources,
                character,
                onConfirm = { characterToDelete ->
                    screenState.charactersFavorites.remove(characterToDelete)
                },
                onDismiss = {
                    screenState.run {
                        characterToDelete = null
                        isDeleteDialogOpen = false
                    }
                }
            )
        }
    }
    CharactersScreenContent(context, resources, result, screenState)
}

@Composable
fun Loading(resources: Resources) {
    Container(alignment = Alignment.Center, expanded = true) {
        Text(
            resources.getString(R.string.msg_loading),
            style = +themeTextStyle { h6 }
        )
    }
}

@Composable
fun CharactersScreenContent(
    context: Context,
    resources: Resources,
    result: ListCharactersResult,
    screenState: CharactersScreenState
) {
    FlexColumn {
        DrawShape(shape = RectangleShape, color = Color(0xfafafa))
        inflexible {
            Tabs(resources = resources,
                selectedTab = screenState.selectedTab,
                onSelected = { index ->
                    screenState.selectedTab = index
                })
        }
        expanded(1f) {
            when (screenState.selectedTab) {
                0 -> CharactersList(resources, result.characters) { character ->
                    screenState.charactersFavorites.add(character)
                    Toast.makeText(
                        context,
                        resources.getString(
                            R.string.msg_added_favorites,
                            character.name
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                1 -> CharactersList(
                    resources,
                    screenState.charactersFavorites
                ) { character ->
                    screenState.run {
                        characterToDelete = character
                        isDeleteDialogOpen = true
                    }
                }
            }
        }
    }
    BottomRightFab()
}

@Composable
fun DeleteFavCharacterDialog(
    resources: Resources,
    character: Character,
    onConfirm: (Character) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onCloseRequest = onDismiss,
        title = {
            Text(
                text = resources.getString(
                    R.string.msg_fav_delete_title
                ),
                style = +themeTextStyle { h6 }
            )
        },
        text = {
            Text(
                text = resources.getString(
                    R.string.msg_fav_delete_message, character.name
                ),
                style = +themeTextStyle { body2 }
            )
        },
        confirmButton = {
            Button(
                text = resources.getString(
                    R.string.msg_fav_delete_confirm
                ),
                style = ContainedButtonStyle(),
                onClick = {
                    onConfirm(character)
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Button(
                text = resources.getString(
                    R.string.msg_fav_delete_cancel
                ),
                style = TextButtonStyle(),
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun Tabs(
    resources: Resources,
    selectedTab: Int,
    onSelected: (Int) -> Unit
) {
    TabRow(
        items = listOf(
            resources.getString(R.string.tab_characters),
            resources.getString(R.string.tab_favorites)
        ),
        selectedIndex = selectedTab,
        tab = { index, string ->
            Tab(
                text = string,
                selected = selectedTab == index,
                onSelected = {
                    onSelected(index)
                }
            )
        }
    )
}

@Composable
fun CharactersList(
    resources: Resources,
    characters: Collection<Character>?,
    action: (Character) -> Unit
) {
    if (characters == null || characters.isEmpty()) {
        Container(expanded = true, alignment = Alignment.Center) {
            Text(
                resources.getString(R.string.msg_characters_list_empty),
                style = +themeTextStyle { h6 }
            )
        }
        return
    }
    Container(expanded = true, alignment = Alignment.TopLeft) {
        VerticalScroller {
            Column {
                characters.forEach { character ->
                    CharacterItem(resources, character, action)
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    resources: Resources,
    character: Character,
    action: (Character) -> Unit
) {
    Container(
        modifier = Spacing(top = 16.dp, left = 16.dp, right = 16.dp)
    ) {
        Card(shape = RoundedCornerShape(4.dp)) {
            Ripple(bounded = true) {
                Clickable(onClick = {
                    action(character)
                }) {
                    CharacterItemContent(resources, character)
                }
            }
        }
    }
}

@Composable
fun CharacterItemContent(
    resources: Resources,
    character: Character
) {
    Row(mainAxisSize = LayoutSize.Expand) {
        Image(url = character.image, width = 96.dp, height = 144.dp)
        Column(
            modifier = Spacing(16.dp),
            mainAxisSize = LayoutSize.Expand,
            crossAxisSize = LayoutSize.Expand
        ) {
            Text(
                text = character.name,
                style = (+themeTextStyle { h6 })
                    .withOpacity(0.87f)
            )
            Text(
                text = character.species,
                style = (+themeTextStyle { body2 })
                    .withOpacity(0.87f)
            )
        }
    }
}

@Composable
fun BottomRightFab() {
    Container(
        expanded = true,
        alignment = Alignment.BottomRight,
        padding = EdgeInsets(all = 16.dp)
    ) {
        FloatingActionButton(
            color = Color.Red,
            onClick = {
                // TODO
            }
        ) {
            Container(width = 24.dp, height = 24.dp) {
                DrawVector(
                    +vectorResource(R.drawable.ic_baseline_add_24)
                )
            }
        }
    }
}
