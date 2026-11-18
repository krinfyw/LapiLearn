package com.example.lapilearn

import com.example.lapilearn.data.repository.*
import com.example.lapilearn.data.local.entity.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ActividadRepositoryTest : RepositoryTestBase() {
    @Test
    fun `catalogo y consulta convierten todos los campos`() = runTest {
        coEvery { actividades.porId(1) } returns actividad()
        coEvery { actividades.porId(99) } returns null
        coEvery { estudiantes.preguntas(1) } returns listOf(pregunta())
        val repo = ActividadRepository(db)
        repo.actividades.first() shouldBe listOf(actividad().toModel())
        repo.porId(1) shouldBe actividad().toModel()
        repo.porId(99) shouldBe null
        repo.preguntas(1) shouldBe listOf(pregunta())
    }

    @Test
    fun `crear recorta campos y siempre guarda borrador`() = runTest {
        coEvery { actividades.insertar(any()) } returns 7
        ActividadRepository(db).agregar(actividad().toModel().copy(titulo = " Sumas ", objetivo = " Sumar ")) shouldBe 7L
        coVerify { actividades.insertar(match { it.titulo == "Sumas" && it.objetivo == "Sumar" && it.estado == "Borrador" && it.intentosPermitidos == 2 }) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["0"])
    fun `intentos invalidos impiden crear`(valor: String) = runTest {
        shouldThrow<IllegalArgumentException> { ActividadRepository(db).agregar(actividad().toModel().copy(intentos = valor)) }
        coVerify(exactly = 0) { actividades.insertar(any()) }
    }

    @Test
    fun `campos obligatorios y actividad inexistente impiden guardar`() = runTest {
        val repo = ActividadRepository(db)
        shouldThrow<IllegalArgumentException> { repo.agregar(actividad().toModel().copy(titulo = " ")) }
        shouldThrow<IllegalArgumentException> { repo.agregar(actividad().toModel().copy(objetivo = " ")) }
        shouldThrow<IllegalArgumentException> { repo.actualizar(actividad().toModel().copy(objetivo = " ")) }
        coEvery { actividades.porId(1) } returns null
        shouldThrow<IllegalArgumentException> { repo.actualizar(actividad().toModel()) }
        coVerify(exactly = 0) { actividades.actualizar(any()) }
    }

    @Test
    fun `disponible requiere preguntas pero borrador puede estar vacio`() = runTest {
        coEvery { actividades.porId(1) } returns actividad()
        coEvery { actividades.actualizar(any()) } just Runs
        coEvery { estudiantes.preguntas(1) } returns emptyList()
        val repo = ActividadRepository(db)
        shouldThrow<IllegalArgumentException> { repo.actualizar(actividad().toModel()) }
        coVerify(exactly = 0) { actividades.actualizar(any()) }
        repo.actualizar(actividad().toModel().copy(estado = "Borrador"))
        coEvery { estudiantes.preguntas(1) } returns listOf(pregunta())
        repo.actualizar(actividad().toModel().copy(objetivo = " Objetivo nuevo "))
        coVerify { actividades.actualizar(match { it.estado == "Disponible" && it.objetivo == "Objetivo nuevo" && it.id == 1 }) }
    }

    @Test
    fun `orden de preguntas y proteccion de respuestas historicas`() = runTest {
        coEvery { estudiantes.ultimoOrdenPregunta(1) } returns 4
        coEvery { estudiantes.insertarPregunta(any()) } just Runs
        coEvery { estudiantes.actualizarPregunta(any()) } just Runs
        coEvery { estudiantes.tieneRespuestas(1) } returns true
        val repo = ActividadRepository(db)
        repo.agregarPregunta(pregunta())
        coVerify { estudiantes.insertarPregunta(match { it.orden == 5 && it.actividadId == 1 }) }
        shouldThrow<IllegalArgumentException> { repo.actualizarPregunta(pregunta()) }
        coVerify(exactly = 0) { estudiantes.actualizarPregunta(any()) }
        coEvery { estudiantes.tieneRespuestas(1) } returns false
        repo.actualizarPregunta(pregunta())
        coVerify(exactly = 1) { estudiantes.actualizarPregunta(pregunta()) }
    }

    @Test
    fun `usuarios consultan credenciales del perfil correcto`() = runTest {
        val docente = mockk<com.example.lapilearn.data.local.dao.DocenteDao>()
        val asistente = mockk<com.example.lapilearn.data.local.dao.AsistenteDao>()
        every { db.docenteDao() } returns docente
        every { db.asistenteDao() } returns asistente
        coEvery { docente.login("docente@duoc.cl", "123456") } returns DocenteEntity(1, "docente@duoc.cl", "123456")
        coEvery { asistente.login("asistente@duoc.cl", "123456") } returns null
        val repo = UsuarioRepository(db)
        repo.loginDocente("docente@duoc.cl", "123456")?.id shouldBe 1L
        repo.loginAsistente("asistente@duoc.cl", "123456") shouldBe null
    }
}
