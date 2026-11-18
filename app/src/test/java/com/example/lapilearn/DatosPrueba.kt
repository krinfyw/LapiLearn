package com.example.lapilearn

import com.example.lapilearn.data.local.entity.*
import com.example.lapilearn.model.SesionEstudiante

internal fun actividad() = ActividadEntity(1, "Sumas", "Matemática", "1° básico", "Fácil", "Sumar", 2, "Disponible", "Elige una respuesta")
internal fun pregunta(id: Long = 1, orden: Int = 1) = PreguntaEntity(id, 1, orden, "", "¿2 + 2?", "3", "4", "5", "6", 1, "2 + 2 = 4")
internal fun intento(id: Long = 1, numero: Int = 1, fin: Long? = null, asignacion: Long? = 1) = IntentoEntity(id, 1, numero, 10, fin, asignacion)
internal fun asignacion(id: Long = 1) = AsignacionEntity(id, "Alumno ficticio", 1, "ABC234", "Asistente", 10)
internal fun sesion() = SesionEstudiante(actividad(), listOf(pregunta()), intento(), emptyList())
