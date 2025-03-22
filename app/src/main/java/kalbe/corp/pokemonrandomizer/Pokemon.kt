package kalbe.corp.pokemonrandomizer

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val name: String,
    val url: String,
)

data class PokemonResponse(
    @SerializedName("results") val pokemonLists: List<Pokemon>? = emptyList()
)

data class SinglePokemonResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
)

data class Sprites(
    val back_default: String,
    val back_shiny: String,
    val front_default: String,
    val front_shiny: String,
)