package com.example.lapilearn

import com.example.lapilearn.data.repository.AsignacionRepository
import com.example.lapilearn.utils.AsignacionReglas
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class AsignacionRepositoryTest : RepositoryTestBase() {
    @Test
    fun `colision de codigo se reintenta y se guarda alias recortado`() = runTest {
        coEvery { actividades.porId(1) } returns actividad()
        coEvery { estudiantes.preguntas(1) } returns listOf(pregunta())
        mockkObject(AsignacionReglas)
        every { AsignacionReglas.generarCodigo(any()) } returnsMany listOf("ABC234", "XYZ234")
        coEvery { asignaciones.contarCodigo("ABC234") } returns 1
        coEvery { asignaciones.contarCodigo("XYZ234") } returns 0
        coEvery { asignaciones.insertar(any()) } returns 2
        val repo = AsignacionRepository(db)
        repo.crearAsignacion(1, " Alumno ", "Asistente") shouldBe "XYZ234"
        coVerify(exactly = 1) { asignaciones.insertar(match { it.aliasEstudiante == "Alumno" && it.codigo == "XYZ234" && it.actividadId == 1 && it.asistente == "Asistente" }) }
        repo.actividades.first() shouldBe listOf(actividad())
        repo.asignaciones.first() shouldBe listOf(asignacion())
        repo.intentos.first() shouldBe listOf(intento())
        repo.preguntas.first() shouldBe listOf(pregunta())
        repo.respuestas.first() shouldBe emptyList()
        coEvery { asignaciones.actualizarObservacion(1, "Observación") } just Runs
        repo.actualizarObservacion(1, "Observación")
        coVerify { asignaciones.actualizarObservacion(1, "Observación") }
    }

    @Test
    fun `no asigna actividad inexistente desactivada o sin preguntas`() = runTest {
        val repo = AsignacionRepository(db)
        coEvery { actividades.porId(1) } returns null
        shouldThrow<IllegalArgumentException> { repo.crearAsignacion(1, "Alumno", "Asistente") }
        coEvery { actividades.porId(1) } returns actividad().copy(estado = "Desactivada")
        shouldThrow<IllegalArgumentException> { repo.crearAsignacion(1, "Alumno", "Asistente") }
        coEvery { actividades.porId(1) } returns actividad()
        coEvery { estudiantes.preguntas(1) } returns emptyList()
        shouldThrow<IllegalArgumentException> { repo.crearAsignacion(1, "Alumno", "Asistente") }
        coVerify(exactly = 0) { asignaciones.insertar(any()) }
    }
}
