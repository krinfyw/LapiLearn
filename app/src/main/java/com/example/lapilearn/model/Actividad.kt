package com.example.lapilearn.model

enum class Modulo(val etiqueta: String) {
    LECTURA("Lectura"),
    MATEMATICA("Matemática")
}

enum class Dificultad(val etiqueta: String) {
    BASICA("Básica"),
    MEDIA("Media"),
    AVANZADA("Avanzada")
}

enum class EstadoActividad(val etiqueta: String) {
    BORRADOR("Borrador"),
    DISPONIBLE("Disponible"),
    DESACTIVADA("Desactivada")
}

/**
 * Actividad del catálogo (contenido ficticio / propio, validado por docente o UTP).
 * Solo las actividades en estado DISPONIBLE pueden ser asignadas por una asistente.
 */
data class Actividad(
    val id: Int,
    val nombre: String,
    val modulo: Modulo,
    val curso: Int,                  // 1 a 4 (básico)
    val contenido: String,
    val objetivo: String,
    val dificultad: Dificultad,
    val instrucciones: String,
    val cantidadPreguntas: Int,
    val intentosPermitidos: Int,
    val estado: EstadoActividad
)
