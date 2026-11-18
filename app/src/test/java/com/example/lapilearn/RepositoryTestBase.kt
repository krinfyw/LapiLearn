package com.example.lapilearn

import androidx.room.withTransaction
import com.example.lapilearn.data.local.AppDatabase
import com.example.lapilearn.data.local.dao.*
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach

internal abstract class RepositoryTestBase {
    protected val db = mockk<AppDatabase>()
    protected val actividades = mockk<ActividadDao>()
    protected val estudiantes = mockk<EstudianteDao>()
    protected val asignaciones = mockk<AsignacionDao>()

    @BeforeEach
    fun prepararDao() {
        every { db.actividadDao() } returns actividades
        every { db.estudianteDao() } returns estudiantes
        every { db.asignacionDao() } returns asignaciones
        every { actividades.observarTodas() } returns flowOf(listOf(actividad()))
        every { asignaciones.observarTodas() } returns flowOf(listOf(asignacion()))
        every { asignaciones.observarIntentos() } returns flowOf(listOf(intento()))
        every { asignaciones.observarPreguntas() } returns flowOf(listOf(pregunta()))
        every { asignaciones.observarRespuestas() } returns flowOf(emptyList())
        // Se ejecuta la lógica del bloque. La atomicidad y SQL de Room necesitan pruebas de integración.
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { db.withTransaction(any<suspend () -> Any?>()) } coAnswers {
            secondArg<suspend () -> Any?>().invoke()
        }
    }

    @AfterEach
    fun limpiarMocks() { unmockkAll() }
}
