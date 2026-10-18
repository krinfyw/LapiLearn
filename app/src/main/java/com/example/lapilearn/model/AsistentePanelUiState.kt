package com.example.lapilearn.model

data class AsistentePanelUiState(
    // Filtros (siempre los elige la asistente; la app no decide el nivel sola)
    val modulo: Modulo? = null,
    val curso: Int? = null,
    val dificultad: Dificultad? = null,

    // Datos ya filtrados / ordenados para mostrar
    val actividades: List<Actividad> = emptyList(),
    val asignaciones: List<Asignacion> = emptyList(),

    // Diálogo "asignar actividad"
    val actividadSeleccionada: Actividad? = null,
    val alias: String = "",
    val errorAlias: String? = null,
    val codigoGenerado: String? = null,

    // Diálogo "observación"
    val asignacionEnObservacion: Asignacion? = null,
    val textoObservacion: String = "",
    val errorObservacion: String? = null
)
