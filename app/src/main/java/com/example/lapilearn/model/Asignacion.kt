package com.example.lapilearn.model

enum class EstadoAsignacion(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_DESARROLLO("En desarrollo"),
    FINALIZADA("Finalizada")
}

/**
 * Actividad asignada por una asistente a un estudiante FICTICIO.
 * El estudiante entra con [codigo] (temporal). No contiene notas ni diagnósticos.
 * Los datos de la actividad se copian (nombre, módulo, curso, dificultad) para que
 * la asignación siga siendo legible aunque el catálogo cambie.
 */
data class Asignacion(
    val id: Int,
    val aliasEstudiante: String,
    val actividadId: Int,
    val nombreActividad: String,
    val modulo: Modulo,
    val curso: Int,
    val dificultad: Dificultad,
    val asistenteResponsable: String,
    val fecha: String,
    val codigo: String,
    val estado: EstadoAsignacion = EstadoAsignacion.PENDIENTE,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val intentos: Int = 0,
    val observacion: String = ""
)
