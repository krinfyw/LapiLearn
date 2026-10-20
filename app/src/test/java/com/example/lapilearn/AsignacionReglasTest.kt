package com.example.lapilearn

import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.data.local.entity.AsignacionEntity
import com.example.lapilearn.data.local.entity.IntentoEntity
import com.example.lapilearn.data.local.entity.RespuestaEntity
import com.example.lapilearn.model.EstadoAsignacion
import com.example.lapilearn.utils.AsignacionReglas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AsignacionReglasTest {

    @Test
    fun aliasVacioEsInvalido() {
        assertNotNull(AsignacionReglas.validarAlias("   "))
    }

    @Test
    fun aliasConSimbolosEsInvalido() {
        assertNotNull(AsignacionReglas.validarAlias("Ana@"))
    }

    @Test
    fun aliasFicticioValido() {
        assertNull(AsignacionReglas.validarAlias("Estudiante D"))
        assertNull(AsignacionReglas.validarAlias("Ñandú 2"))
    }

    @Test
    fun observacionVaciaEsInvalida() {
        assertNotNull(AsignacionReglas.validarObservacion(" "))
        assertNull(AsignacionReglas.validarObservacion("Se concentró bien."))
    }

    @Test
    fun codigoTieneSeisCaracteresLegibles() {
        repeat(300) {
            val codigo = AsignacionReglas.generarCodigo()
            assertEquals(AsignacionReglas.LARGO_CODIGO, codigo.length)
            assertTrue(codigo.all { it in AsignacionReglas.ALFABETO_CODIGO })
        }
        assertTrue(AsignacionReglas.ALFABETO_CODIGO.none { it in "01OI" })
    }

    @Test
    fun estadoSegunUltimoIntento() {
        assertEquals(EstadoAsignacion.PENDIENTE, AsignacionReglas.estado(null))
        assertEquals(
            EstadoAsignacion.EN_DESARROLLO,
            AsignacionReglas.estado(IntentoEntity(actividadId = 1, numero = 1, iniciadoEn = 0L))
        )
        assertEquals(
            EstadoAsignacion.FINALIZADA,
            AsignacionReglas.estado(IntentoEntity(actividadId = 1, numero = 1, iniciadoEn = 0L, finalizadoEn = 5L))
        )
    }

    @Test
    fun resumenCuentaAciertosYErroresDelUltimoIntento() {
        val actividad = ActividadEntity(
            id = 1, titulo = "T", modulo = "Lectura", curso = "1° básico", dificultad = "Fácil",
            objetivo = "O", intentosPermitidos = 2, estado = "Disponible", codigo = "X", instrucciones = "I"
        )
        val asignacion = AsignacionEntity(
            id = 1, aliasEstudiante = "A", actividadId = 1, codigo = "ABC234", asistente = "S", creadaEn = 0L
        )
        val intentos = listOf(
            IntentoEntity(id = 10, actividadId = 1, numero = 1, iniciadoEn = 0L, finalizadoEn = 5L, asignacionId = 1),
            IntentoEntity(id = 11, actividadId = 1, numero = 2, iniciadoEn = 6L, finalizadoEn = null, asignacionId = 1)
        )
        val respuestas = listOf(
            RespuestaEntity(intentoId = 10, preguntaId = 1, alternativa = 0, esCorrecta = false, respondidaEn = 1L),
            RespuestaEntity(intentoId = 11, preguntaId = 1, alternativa = 2, esCorrecta = true, respondidaEn = 7L)
        )

        val resumen = AsignacionReglas.resumir(listOf(asignacion), listOf(actividad), intentos, respuestas).single()

        assertEquals(EstadoAsignacion.EN_DESARROLLO, resumen.estado)
        assertEquals(2, resumen.intentosUsados)
        assertEquals(1, resumen.aciertos)
        assertEquals(0, resumen.errores)
    }
}
