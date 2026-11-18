package com.example.lapilearn

import com.example.lapilearn.data.local.entity.RespuestaEntity
import com.example.lapilearn.utils.AsignacionReglas
import com.example.lapilearn.model.EstadoAsignacion
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import org.junit.jupiter.api.Test

internal class AsignacionReglasTest {
    @Test
    fun `alias y observaciones aceptan datos ficticios y rechazan vacios y simbolos`() {
        AsignacionReglas.validarAlias(" ") shouldBe "Escribe un alias ficticio para el estudiante"
        AsignacionReglas.validarAlias(" A ") shouldBe "El alias es muy corto"
        AsignacionReglas.validarAlias("Alumno!") shouldBe "Usa solo letras y números (sin símbolos)"
        AsignacionReglas.validarAlias("Niño 12") shouldBe null
        AsignacionReglas.validarObservacion(" ") shouldBe "Escribe una observación general"
        AsignacionReglas.validarObservacion("Practicar sumas") shouldBe null
        val codigo = AsignacionReglas.generarCodigo(Random(12))
        codigo.length shouldBe 6
        codigo.all { it in AsignacionReglas.ALFABETO_CODIGO } shouldBe true
        codigo.none { it in "0O1I" } shouldBe true
    }

    @Test
    fun `resumen usa ultimo intento y mantiene estudiantes separados`() {
        val resultado = AsignacionReglas.resumir(
            listOf(asignacion(), asignacion(2), asignacion(3)),
            listOf(actividad()),
            listOf(intento(1, 1, 20), intento(2, 2), intento(3, 1, 20, 2)),
            listOf(RespuestaEntity(1, 1, 0, false, 15), RespuestaEntity(2, 1, 1, true, 15), RespuestaEntity(3, 1, 0, false, 15))
        )
        resultado[0].estado shouldBe EstadoAsignacion.EN_DESARROLLO
        resultado[0].intentosUsados shouldBe 2
        resultado[0].aciertos shouldBe 1
        resultado[0].errores shouldBe 0
        resultado[1].estado shouldBe EstadoAsignacion.FINALIZADA
        resultado[1].errores shouldBe 1
        resultado[2].estado shouldBe EstadoAsignacion.PENDIENTE
        resultado[2].aciertos shouldBe 0
        AsignacionReglas.resumir(listOf(asignacion()), emptyList(), emptyList(), emptyList()).single().actividad shouldBe null
        AsignacionReglas.resumir(emptyList(), emptyList(), emptyList(), emptyList()) shouldBe emptyList()
    }

    @Test
    fun `detalle ordena intentos y preguntas y descarta respuestas ajenas o huerfanas`() {
        val resultado = AsignacionReglas.detallar(
            1,
            listOf(intento(2, 2), intento(1, 1, 20), intento(3, 1, 20, 2)),
            listOf(pregunta(2, 2), pregunta()),
            listOf(RespuestaEntity(1, 2, -1, false, 15), RespuestaEntity(1, 1, 1, true, 15), RespuestaEntity(1, 99, 1, true, 15), RespuestaEntity(3, 1, 0, false, 15))
        )
        resultado.map { it.numero } shouldBe listOf(1, 2)
        resultado.first().finalizado shouldBe true
        resultado.last().finalizado shouldBe false
        resultado.first().respuestas.map { it.orden } shouldBe listOf(1, 2)
        resultado.first().respuestas[0].elegida shouldBe "B) 4"
        resultado.first().respuestas[0].correcta shouldBe "B) 4"
        resultado.first().respuestas[1].elegida shouldBe "Sin respuesta"
        resultado.last().respuestas shouldBe emptyList()
    }
}
