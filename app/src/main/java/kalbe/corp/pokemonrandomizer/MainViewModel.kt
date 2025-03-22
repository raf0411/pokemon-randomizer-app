package kalbe.corp.pokemonrandomizer

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _pokemonListsState = mutableStateOf(PokemonListsState())
    private val _singlePokemonState = mutableStateOf(SinglePokemonState())
    private val _pokemonSprites = mutableStateOf<Map<Int, Sprites>>(emptyMap())
    val pokemonListsState: State<PokemonListsState> = _pokemonListsState
    val singlePokemonState: State<SinglePokemonState> = _singlePokemonState

    init {
        fetchPokemonLists()
        fetchSinglePokemon(1)
    }

    private fun fetchPokemonLists() {
        viewModelScope.launch {
            try {
                val response = pokemonService.getPokemonLists()
                val pokemonList = response.pokemonLists ?: emptyList()

                _pokemonListsState.value = _pokemonListsState.value.copy(
                    pokemonLists = pokemonList,
                    loading = false,
                    error = null,
                )

                fetchPokemonSprites(pokemonList)
            } catch (e: Exception) {
                _pokemonListsState.value = _pokemonListsState.value.copy(
                    loading = false,
                    error = "ERROR FETCHING POKEMON: ${e.message}"
                )
                Log.e("ERROR", _pokemonListsState.value.error ?: "Unknown error")
            }
        }
    }

    private fun fetchPokemonSprites(pokemonList: List<Pokemon>) {
        viewModelScope.launch {
            val spriteMap = mutableMapOf<Int, Sprites>()

            pokemonList.forEach { pokemon ->
                val id = pokemon.url.trimEnd('/').split("/").last().toIntOrNull()
                if (id != null) {
                    try {
                        val response = pokemonService.getSinglePokemon(id)
                        spriteMap[id] = response.sprites
                    } catch (e: Exception) {
                        Log.e("ERROR", "Failed to fetch sprite for Pokemon ID: $id")
                    }
                }
            }

            _pokemonSprites.value = spriteMap
        }
    }

    private fun fetchSinglePokemon(id: Int) {
        viewModelScope.launch {
            try {
                val response = pokemonService.getSinglePokemon(id)
                _singlePokemonState.value = _singlePokemonState.value.copy(
                    pokemon = response,
                    loading = false,
                    error = null,
                )
            } catch (e: Exception) {
                _singlePokemonState.value = _singlePokemonState.value.copy(
                    loading = false,
                    error = "ERROR FETCHING POKEMON: ${e.message}"
                )
                Log.e("ERROR", _singlePokemonState.value.error ?: "Unknown error")
            }
        }
    }

    fun getPokemonSprites(id: Int): Sprites? {
        return _pokemonSprites.value[id]
    }

    fun randomizePokemon() {
        val randomId = (1..151).random()
        fetchSinglePokemon(randomId)
    }

    data class PokemonListsState(
        val loading: Boolean = true,
        val pokemonLists: List<Pokemon> = emptyList(),
        val error: String? = null,
    )

    data class SinglePokemonState(
        val loading: Boolean = true,
        val pokemon: SinglePokemonResponse? = null,
        val error: String? = null,
    )
}