package com.example.lapilearn.data.repository

import com.example.lapilearn.model.Pokemon
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException
import kotlin.getValue

object PokemonRepository {
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }

    suspend fun obtener(aciertos: Int, total: Int): Pokemon? = try {
        val respuesta = api.obtenerPokemon(pokemonParaResultado(aciertos, total))
        respuesta.name?.let { Pokemon(it, respuesta.sprites?.frontal) }
    } catch (_: IOException) {
        null
    } catch (_: HttpException) {
        null
    } catch (_: JsonParseException) {
        null
    }

    internal fun pokemonParaResultado(aciertos: Int, total: Int): String = when {
        aciertos == 0 -> "bulbasaur"
        aciertos == total -> "pikachu"
        else -> "charmander"
    }
}

private interface PokeApi {
    @GET("pokemon/{nombre}/")
    suspend fun obtenerPokemon(@Path("nombre") nombre: String): PokemonRespuesta
}

private data class PokemonRespuesta(val name: String?, val sprites: PokemonSprites?)

private data class PokemonSprites(@SerializedName("front_default") val frontal: String?)
