package kalbe.corp.pokemonrandomizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun PokemonScreen(){
    val pokemonViewModel: MainViewModel = viewModel()
    val viewState by pokemonViewModel.pokemonListsState
    val pokemonViewState by pokemonViewModel.singlePokemonState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box{
            when {
                pokemonViewState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                pokemonViewState.error != null -> {
                    Text("ERROR OCCURRED!", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    pokemonViewState.pokemon?.let {
                        RandomizedPokemonScreen(
                            singlePokemon = it,
                            onRandomizeClick = { pokemonViewModel.randomizePokemon() })
                    }
                }
            }
        }
        Box{
            when {
                viewState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewState.error != null -> {
                    Text("ERROR OCCURRED!", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PokemonListScreen(pokemonLists = viewState.pokemonLists, pokemonViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun RandomizedPokemonScreen(
    singlePokemon: SinglePokemonResponse,
    onRandomizeClick: () -> Unit,
){
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.Center),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = singlePokemon.sprites.front_default,
                contentDescription = "pokemon sprite",
                modifier = Modifier.aspectRatio(2f)
            )

            Text(text = "Randomized Pokemon", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ID: ${singlePokemon.id}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Name: ${singlePokemon.name.capitalize()}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Height: ${singlePokemon.height}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Weight: ${singlePokemon.weight}")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {onRandomizeClick()}) { Text("Randomize") }
        }
    }
}

@Composable
fun PokemonListScreen(pokemonLists: List<Pokemon>, pokemonViewModel: MainViewModel) {
    Row {
        LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
            items(pokemonLists) { pokemon ->
                val id = pokemon.url.trimEnd('/').split("/").last().toIntOrNull() ?: 0
                pokemonViewModel.getPokemonSprites(id)
                    ?.let { PokemonItem(pokemon = pokemon, sprites = it) }
            }
        }
    }
}

@Composable
fun PokemonItem(pokemon: Pokemon, sprites: Sprites) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Yellow)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = sprites.front_default,
            contentDescription = "pokemon sprite",
            modifier = Modifier.aspectRatio(2f)
        )

        Text(
            text = pokemon.name.capitalize(),
            color = Color.Black,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}