package com.example.lapilearn

import com.example.lapilearn.data.repository.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class PokemonRepositoryTest {
    @Test
    fun `selecciona pokemon para cero algunos y todos incluso una pregunta`() {
        PokemonRepository.pokemonParaResultado(0, 3) shouldBe "bulbasaur"
        PokemonRepository.pokemonParaResultado(1, 3) shouldBe "charmander"
        PokemonRepository.pokemonParaResultado(3, 3) shouldBe "pikachu"
        PokemonRepository.pokemonParaResultado(0, 1) shouldBe "bulbasaur"
        PokemonRepository.pokemonParaResultado(1, 1) shouldBe "pikachu"
    }

    @Test
    fun `Gson convierte la respuesta y el repositorio consulta el pokemon elegido`() = runTest {
        val api = mockk<PokeApi>()
        val json = """{"name":"pikachu","sprites":{"front_default":"https://ejemplo.cl/pikachu.png"}}"""
        val respuesta = Gson().fromJson(json, PokemonRespuesta::class.java)
        coEvery { api.obtenerPokemon("pikachu") } returns respuesta

        val pokemon = PokemonRepository.obtener(3, 3, api)

        pokemon?.nombre shouldBe "pikachu"
        pokemon?.imagenUrl shouldBe "https://ejemplo.cl/pikachu.png"
        coVerify(exactly = 1) { api.obtenerPokemon("pikachu") }
        coEvery { api.obtenerPokemon("bulbasaur") } returns PokemonRespuesta("bulbasaur", null)
        PokemonRepository.obtener(0, 3, api)?.imagenUrl shouldBe null
        coEvery { api.obtenerPokemon("charmander") } returns PokemonRespuesta(null, null)
        PokemonRepository.obtener(1, 3, api) shouldBe null
    }

    @Test
    fun `sin internet devuelve null y cancelacion se propaga`() = runTest {
        val api = mockk<PokeApi>()
        coEvery { api.obtenerPokemon(any()) } throws IOException("Sin conexión")
        PokemonRepository.obtener(0, 1, api) shouldBe null
        coEvery { api.obtenerPokemon(any()) } throws HttpException(Response.error<PokemonRespuesta>(503, "No disponible".toResponseBody()))
        PokemonRepository.obtener(0, 1, api) shouldBe null
        coEvery { api.obtenerPokemon(any()) } throws JsonSyntaxException("JSON inválido")
        PokemonRepository.obtener(0, 1, api) shouldBe null
        coEvery { api.obtenerPokemon(any()) } throws CancellationException()
        shouldThrow<CancellationException> { PokemonRepository.obtener(1, 1, api) }
    }
}
