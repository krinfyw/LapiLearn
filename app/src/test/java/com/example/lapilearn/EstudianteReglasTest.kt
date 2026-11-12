package com.example.lapilearn

import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.data.local.entity.IntentoEntity
import com.example.lapilearn.data.local.entity.PreguntaEntity
import com.example.lapilearn.model.SesionEstudiante
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EstudianteReglasTest {
    private val pregunta = PreguntaEntity(
        actividadId = 1,
        orden = 1,
        textoApoyo = "",
        enunciado = "¿Cuánto es 2 + 2?",
        opcionA = "3",
        opcionB = "4",
        opcionC = "5",
        opcionD = "6",
        correcta = 1,
        retroalimentacion = "2 + 2 es 4."
    )

    @Test
    fun `la alternativa correcta se marca como acierto`() {
        pregunta.esCorrecta(alternativa = 1) shouldBe true
    }

    @Test
    fun `una alternativa incorrecta no cuenta como acierto`() {
        pregunta.esCorrecta(alternativa = 0) shouldBe false
    }

    @Test
    fun `el limite incluye el primer intento y bloquea otro al agotarse`() {
        val actividad = ActividadEntity(
            id = 1,
            titulo = "Sumas",
            modulo = "Matemática",
            curso = "1.º básico",
            dificultad = "Fácil",
            objetivo = "Practicar sumas",
            intentosPermitidos = 2,
            estado = "Disponible",
            instrucciones = "Elige la respuesta correcta."
        )
        val intento = IntentoEntity(
            actividadId = actividad.id,
            numero = 1,
            iniciadoEn = 0,
            finalizadoEn = 1
        )
        val sesion = SesionEstudiante(actividad, listOf(pregunta), intento, emptyList())

        sesion.quedanIntentos shouldBe true
        sesion.copy(intento = intento.copy(numero = 2)).quedanIntentos shouldBe false
    }
}
