package com.example.lapilearn

import com.example.lapilearn.data.local.entity.*
import com.example.lapilearn.data.repository.EstudianteRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class EstudianteRepositoryTest : RepositoryTestBase() {
    private var actual = intento()
    private val respuestas = mutableListOf<RespuestaEntity>()

    @BeforeEach
    fun prepararSesion() {
        coEvery { actividades.porId(1) } returns actividad()
        coEvery { asignaciones.porCodigo("ABC234") } returns asignacion()
        coEvery { estudiantes.preguntas(1) } returns listOf(pregunta())
        coEvery { estudiantes.intento(any()) } answers { actual.copy(id = firstArg()) }
        coEvery { estudiantes.respuestas(any()) } answers { respuestas.toList() }
        coEvery { estudiantes.ultimoIntentoDeAsignacion(1) } answers { actual }
        coEvery { estudiantes.insertarRespuesta(any()) } answers { respuestas.add(firstArg()); Unit }
        coEvery { estudiantes.finalizar(any(), any()) } answers { actual = actual.copy(finalizadoEn = secondArg()); Unit }
    }

    @Test
    fun `normaliza codigo y recupera intento sin gastar otro`() = runTest {
        EstudianteRepository(db).buscarSesion(" abc234 ").intento.id shouldBe 1L
        coVerify(exactly = 0) { estudiantes.insertarIntento(any()) }
        coEvery { estudiantes.ultimoIntentoDeAsignacion(1) } returns null
        coEvery { estudiantes.insertarIntento(any()) } returns 2
        EstudianteRepository(db).buscarSesion("ABC234").intento.id shouldBe 2L
        coVerify { estudiantes.insertarIntento(match { it.numero == 1 && it.asignacionId == 1L }) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1])
    fun `guarda acierto o error y finaliza la ultima pregunta`(alternativa: Int) = runTest {
        val resultado = EstudianteRepository(db).guardarRespuesta(1, 1, alternativa)
        resultado.finalizada shouldBe true
        resultado.aciertos shouldBe if (alternativa == 1) 1 else 0
        resultado.respuestas.single().alternativa shouldBe alternativa
        coVerify(exactly = 1) { estudiantes.finalizar(1, any()) }
    }

    @Test
    fun `respuesta parcial mantiene intento abierto y rechaza duplicada`() = runTest {
        coEvery { estudiantes.preguntas(1) } returns listOf(pregunta(), pregunta(2, 2))
        val repo = EstudianteRepository(db)
        repo.guardarRespuesta(1, 1, 1).finalizada shouldBe false
        shouldThrow<IllegalArgumentException> { repo.guardarRespuesta(1, 1, 1) }
        coVerify(exactly = 0) { estudiantes.finalizar(any(), any()) }
        coVerify(exactly = 1) { estudiantes.insertarRespuesta(any()) }
    }

    @Test
    fun `reintento requiere finalizar y respeta limite`() = runTest {
        val repo = EstudianteRepository(db)
        shouldThrow<IllegalArgumentException> { repo.crearReintento(1) }
        actual = intento(numero = 2, fin = 20)
        shouldThrow<IllegalArgumentException> { repo.crearReintento(1) }
        coVerify(exactly = 0) { estudiantes.insertarIntento(any()) }
        actual = intento(fin = 20)
        coEvery { estudiantes.insertarIntento(any()) } answers { actual = firstArg<IntentoEntity>().copy(id = 2); 2L }
        val nueva = repo.crearReintento(1)
        nueva.intento.numero shouldBe 2
        nueva.finalizada shouldBe false
        coVerify { estudiantes.insertarIntento(match { it.numero == 2 && it.asignacionId == 1L }) }
    }

    @Test
    fun `peticion repetida recupera ultimo intento y admite registros antiguos`() = runTest {
        val repo = EstudianteRepository(db)
        actual = intento(fin = 20)
        coEvery { estudiantes.ultimoIntentoDeAsignacion(1) } returns intento(2, 2)
        repo.crearReintento(1).intento.id shouldBe 2L
        coVerify(exactly = 0) { estudiantes.insertarIntento(any()) }
        actual = intento(fin = 20, asignacion = null)
        coEvery { estudiantes.ultimoIntento(1) } answers { actual }
        coEvery { estudiantes.insertarIntento(any()) } answers { actual = firstArg<IntentoEntity>().copy(id = 3); 3L }
        repo.crearReintento(1).intento.asignacionId shouldBe null
    }
}
