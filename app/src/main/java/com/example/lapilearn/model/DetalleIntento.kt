package com.example.lapilearn.model

// Una respuesta del estudiante, lista para mostrar a la asistente.
data class DetalleRespuesta(
    val orden: Int,
    val enunciado: String,
    val elegida: String,
    val correcta: String,
    val esCorrecta: Boolean
)

// Todas las respuestas de un intento.
data class DetalleIntento(
    val numero: Int,
    val finalizado: Boolean,
    val respuestas: List<DetalleRespuesta>
)
