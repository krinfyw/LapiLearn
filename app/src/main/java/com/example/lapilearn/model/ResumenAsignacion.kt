package com.example.lapilearn.model

import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.data.local.entity.AsignacionEntity

enum class EstadoAsignacion(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_DESARROLLO("En desarrollo"),
    FINALIZADA("Finalizada")
}

/**
 * Lo que ve la asistente de cada asignación. Aciertos y errores son del ÚLTIMO intento.
 * Es un resumen de práctica: no es una nota ni un diagnóstico.
 */
data class ResumenAsignacion(
    val asignacion: AsignacionEntity,
    val actividad: ActividadEntity?,
    val estado: EstadoAsignacion,
    val intentosUsados: Int,
    val aciertos: Int,
    val errores: Int
)
