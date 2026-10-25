package com.example.lapilearn.model

import com.example.lapilearn.data.local.entity.ActividadEntity

data class AsistentePanelUiState(
    // Filtros: siempre los elige la asistente; la app no decide el nivel sola.
    val modulo: String? = null,
    val curso: String? = null,
    val dificultad: String? = null,

    // Datos ya filtrados / calculados para mostrar
    val actividades: List<ActividadEntity> = emptyList(),
    val asignaciones: List<ResumenAsignacion> = emptyList(),

    // Diálogo "asignar actividad"
    val actividadSeleccionada: ActividadEntity? = null,
    val alias: String = "",
    val errorAlias: String? = null,
    val guardando: Boolean = false,
    val codigoGenerado: String? = null,

    // Pantalla de respuestas detalladas: asignación que se está mirando
    val asignacionEnDetalleId: Long? = null,

    // Diálogo "observación"
    val asignacionEnObservacion: ResumenAsignacion? = null,
    val textoObservacion: String = "",
    val errorObservacion: String? = null
) {
    val hayFiltros: Boolean get() = modulo != null || curso != null || dificultad != null
}
