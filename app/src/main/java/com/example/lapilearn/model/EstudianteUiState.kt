package com.example.lapilearn.model

import com.example.lapilearn.data.local.entity.PreguntaEntity

enum class PasoEstudiante { INSTRUCCIONES, PREGUNTA, RETROALIMENTACION, RESULTADO }

data class EstudianteUiState(
    val codigo: String = "",
    val errores: EstudianteErrores = EstudianteErrores(),
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: SesionEstudiante? = null,
    val paso: PasoEstudiante = PasoEstudiante.INSTRUCCIONES,
    val seleccion: Int? = null,
    val confirmar: Boolean = false,
    val preguntaRespondida: PreguntaEntity? = null
) {
    val preguntaActual: PreguntaEntity? get() = sesion?.let { s ->
        s.preguntas.firstOrNull { p -> s.respuestas.none { it.preguntaId == p.id } }
    }
}
