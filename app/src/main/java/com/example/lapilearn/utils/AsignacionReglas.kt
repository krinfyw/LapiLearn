package com.example.lapilearn.utils

import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.data.local.entity.AsignacionEntity
import com.example.lapilearn.data.local.entity.IntentoEntity
import com.example.lapilearn.data.local.entity.PreguntaEntity
import com.example.lapilearn.data.local.entity.RespuestaEntity
import com.example.lapilearn.model.DetalleIntento
import com.example.lapilearn.model.DetalleRespuesta
import com.example.lapilearn.model.EstadoAsignacion
import com.example.lapilearn.model.ResumenAsignacion
import kotlin.random.Random

/** Reglas puras de las asignaciones (sin Android), para poder probarlas con tests unitarios. */
object AsignacionReglas {

    // Sin 0, O, 1, I para que el código sea fácil de leer y escribir.
    const val ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    const val LARGO_CODIGO = 6
    const val MAX_ALIAS = 20
    const val MAX_OBSERVACION = 200

    private val REGEX_ALIAS = Regex("^[\\p{L}\\p{N} ]+$")

    fun validarAlias(alias: String): String? = when {
        alias.isBlank() -> "Escribe un alias ficticio para el estudiante"
        alias.trim().length < 2 -> "El alias es muy corto"
        !REGEX_ALIAS.matches(alias) -> "Usa solo letras y números (sin símbolos)"
        else -> null
    }

    fun validarObservacion(texto: String): String? =
        if (texto.isBlank()) "Escribe una observación general" else null

    fun generarCodigo(random: Random = Random.Default): String =
        buildString {
            repeat(LARGO_CODIGO) { append(ALFABETO_CODIGO[random.nextInt(ALFABETO_CODIGO.length)]) }
        }

    /** Pendiente: nunca entró. En desarrollo: tiene un intento sin terminar. Finalizada: el último intento terminó. */
    fun estado(ultimoIntento: IntentoEntity?): EstadoAsignacion = when {
        ultimoIntento == null -> EstadoAsignacion.PENDIENTE
        ultimoIntento.finalizadoEn == null -> EstadoAsignacion.EN_DESARROLLO
        else -> EstadoAsignacion.FINALIZADA
    }

    fun resumir(
        asignaciones: List<AsignacionEntity>,
        actividades: List<ActividadEntity>,
        intentos: List<IntentoEntity>,
        respuestas: List<RespuestaEntity>
    ): List<ResumenAsignacion> = asignaciones.map { asignacion ->
        val propios = intentos.filter { it.asignacionId == asignacion.id }
        val ultimo = propios.maxByOrNull { it.numero }
        val delUltimo = if (ultimo == null) emptyList() else respuestas.filter { it.intentoId == ultimo.id }
        ResumenAsignacion(
            asignacion = asignacion,
            actividad = actividades.firstOrNull { it.id == asignacion.actividadId },
            estado = estado(ultimo),
            intentosUsados = propios.size,
            aciertos = delUltimo.count { it.esCorrecta },
            errores = delUltimo.count { !it.esCorrecta }
        )
    }

    /** Arma, intento por intento, qué respondió el estudiante en cada pregunta. */
    fun detallar(
        asignacionId: Long,
        intentos: List<IntentoEntity>,
        preguntas: List<PreguntaEntity>,
        respuestas: List<RespuestaEntity>
    ): List<DetalleIntento> =
        intentos
            .filter { it.asignacionId == asignacionId }
            .sortedBy { it.numero }
            .map { intento ->
                val items = respuestas
                    .filter { it.intentoId == intento.id }
                    .mapNotNull { respuesta ->
                        val pregunta = preguntas.firstOrNull { it.id == respuesta.preguntaId }
                        if (pregunta == null) {
                            null
                        } else {
                            DetalleRespuesta(
                                orden = pregunta.orden,
                                enunciado = pregunta.enunciado,
                                elegida = textoAlternativa(pregunta, respuesta.alternativa),
                                correcta = textoAlternativa(pregunta, pregunta.correcta),
                                esCorrecta = respuesta.esCorrecta
                            )
                        }
                    }
                    .sortedBy { it.orden }
                DetalleIntento(
                    numero = intento.numero,
                    finalizado = intento.finalizadoEn != null,
                    respuestas = items
                )
            }

    // Muestra la alternativa como "B) texto".
    private fun textoAlternativa(pregunta: PreguntaEntity, indice: Int): String {
        val texto = pregunta.alternativas.getOrNull(indice) ?: return "Sin respuesta"
        return "${'A' + indice}) $texto"
    }
}
