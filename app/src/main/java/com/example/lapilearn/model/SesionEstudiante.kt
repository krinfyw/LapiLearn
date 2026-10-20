package com.example.lapilearn.model

import com.example.lapilearn.data.local.entity.*

data class SesionEstudiante(
    val actividad: ActividadEntity,
    val preguntas: List<PreguntaEntity>,
    val intento: IntentoEntity,
    val respuestas: List<RespuestaEntity>
) {
    val finalizada: Boolean get() = intento.finalizadoEn != null
    val aciertos: Int get() = respuestas.count { it.esCorrecta }
}

