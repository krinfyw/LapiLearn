package com.example.lapilearn

import com.example.lapilearn.data.local.entity.RespuestaEntity
import com.example.lapilearn.data.repository.*
import com.example.lapilearn.model.*
import com.example.lapilearn.viewModels.EstudianteViewModel
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal class EstudianteViewModelTest : ViewModelTestBase() {
    private val repo = mockk<EstudianteRepository>()
    private fun modelo() = registrar(EstudianteViewModel(app, repo))

    @Test
    fun `codigo obligatorio y acciones sin sesion no consultan datos`() = runTest {
        val vm = modelo()
        vm.ingresar { error("No debe ingresar") }
        vm.uiState.value.errores.codigo shouldBe "Ingresa el código de tu actividad."
        vm.reintentar()
        vm.confirmarRespuesta()
        vm.seleccionar(0)
        vm.solicitarConfirmacion()
        vm.uiState.value.confirmar shouldBe false
        vm.onCodigoChange("abc234")
        vm.uiState.value.codigo shouldBe "ABC234"
        vm.uiState.value.errores.codigo shouldBe null
        coVerify(exactly = 0) { repo.buscarSesion(any()) }
    }

    @Test
    fun `responder requiere confirmar y transita hasta resultado`() = runTest {
        val inicial = sesion()
        val final = inicial.copy(intento = intento(fin = 20), respuestas = listOf(RespuestaEntity(1, 1, 1, true, 15)))
        coEvery { repo.buscarSesion("ABC234") } returns inicial
        coEvery { repo.guardarRespuesta(1, 1, 1) } returns final
        val vm = modelo()
        vm.onCodigoChange("abc234")
        var ingresos = 0
        vm.ingresar { ingresos++ }
        vm.ingresar { error("Ingreso duplicado") }
        vm.onCodigoChange("OTRO")
        vm.salir()
        runCurrent()
        ingresos shouldBe 1
        vm.uiState.value.paso shouldBe PasoEstudiante.INSTRUCCIONES
        vm.comenzar()
        vm.seleccionar(-1)
        vm.seleccionar(4)
        vm.uiState.value.seleccion shouldBe null
        vm.seleccionar(1)
        vm.confirmarRespuesta()
        coVerify(exactly = 0) { repo.guardarRespuesta(any(), any(), any()) }
        vm.solicitarConfirmacion()
        vm.seleccionar(0)
        vm.uiState.value.seleccion shouldBe 1
        vm.cancelarConfirmacion()
        vm.uiState.value.confirmar shouldBe false
        vm.solicitarConfirmacion()
        vm.confirmarRespuesta()
        vm.confirmarRespuesta()
        runCurrent()
        vm.uiState.value.paso shouldBe PasoEstudiante.RETROALIMENTACION
        vm.uiState.value.sesion?.aciertos shouldBe 1
        vm.uiState.value.preguntaRespondida shouldBe pregunta()
        vm.siguiente()
        vm.uiState.value.paso shouldBe PasoEstudiante.RESULTADO
        vm.uiState.value.seleccion shouldBe null
        coVerify(exactly = 1) { repo.guardarRespuesta(1, 1, 1) }
        vm.salir()
        vm.uiState.value.sesion shouldBe null
    }

    @Test
    fun `siguiente avanza a pregunta pendiente y reintento vuelve a instrucciones`() = runTest {
        val inicial = sesion().copy(preguntas = listOf(pregunta(), pregunta(2, 2)))
        val parcial = inicial.copy(respuestas = listOf(RespuestaEntity(1, 1, 0, false, 15)))
        coEvery { repo.buscarSesion(any()) } returns inicial
        coEvery { repo.guardarRespuesta(any(), any(), any()) } returns parcial
        coEvery { repo.crearReintento(1) } returns sesion().copy(intento = intento(2, 2))
        val vm = modelo()
        vm.onCodigoChange("ABC234")
        vm.ingresar {}
        runCurrent()
        vm.comenzar()
        vm.seleccionar(0)
        vm.solicitarConfirmacion()
        vm.confirmarRespuesta()
        runCurrent()
        vm.siguiente()
        vm.uiState.value.paso shouldBe PasoEstudiante.PREGUNTA
        vm.uiState.value.preguntaActual?.id shouldBe 2L
        vm.reintentar()
        runCurrent()
        vm.uiState.value.sesion?.intento?.numero shouldBe 2
        vm.uiState.value.paso shouldBe PasoEstudiante.INSTRUCCIONES
    }

    @Test
    fun `recuperar intento finalizado muestra resultado y limita reintentos`() = runTest {
        coEvery { repo.buscarSesion(any()) } returns sesion().copy(intento = intento(numero = 2, fin = 20))
        coEvery { repo.crearReintento(1) } throws IllegalArgumentException("Intentos agotados")
        val vm = modelo()
        vm.onCodigoChange("ABC234")
        vm.ingresar {}
        runCurrent()
        vm.uiState.value.paso shouldBe PasoEstudiante.RESULTADO
        vm.reintentar()
        runCurrent()
        vm.uiState.value.error shouldBe "Intentos agotados"
        vm.uiState.value.cargando shouldBe false
        vm.uiState.value.confirmar shouldBe false
    }

    @Test
    fun `pokemon es opcional en la sesion`() = runTest {
        mockkObject(PokemonRepository)
        coEvery { PokemonRepository.obtener(0, 1) } returns Pokemon("bulbasaur", null)
        modelo().cargarPokemon(sesion())?.nombre shouldBe "bulbasaur"
    }
}
