package com.example.lapilearn.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lapilearn.data.CuentasDePrueba
import com.example.lapilearn.data.RepositorioActividades
import com.example.lapilearn.model.Actividad
import com.example.lapilearn.model.Asignacion
import com.example.lapilearn.model.AsistentePanelUiState
import com.example.lapilearn.model.Dificultad
import com.example.lapilearn.model.EstadoActividad
import com.example.lapilearn.model.Modulo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AsistentePanelViewModel : ViewModel() {

    companion object {
        const val MAX_ALIAS = 20
        const val MAX_OBSERVACION = 200
        private val REGEX_ALIAS = Regex("^[\\p{L}\\p{N} ]+$")
    }

    // Estado del formulario (filtros y diálogos). Los datos vienen del repositorio.
    private val _form = MutableStateFlow(AsistentePanelUiState())

    val uiState: StateFlow<AsistentePanelUiState> = combine(
        _form,
        RepositorioActividades.actividades,
        RepositorioActividades.asignaciones
    ) { form, actividades, asignaciones ->
        form.copy(
            actividades = actividades
                .filter { it.estado == EstadoActividad.DISPONIBLE } // solo las autorizadas
                .filter { form.modulo == null || it.modulo == form.modulo }
                .filter { form.curso == null || it.curso == form.curso }
                .filter { form.dificultad == null || it.dificultad == form.dificultad }
                .sortedWith(compareBy({ it.curso }, { it.dificultad })),
            asignaciones = asignaciones.sortedByDescending { it.id }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AsistentePanelUiState())

    // ---------- Filtros (tocar de nuevo el mismo filtro lo quita) ----------

    fun onModuloChange(valor: Modulo) =
        _form.update { it.copy(modulo = if (it.modulo == valor) null else valor) }

    fun onCursoChange(valor: Int) =
        _form.update { it.copy(curso = if (it.curso == valor) null else valor) }

    fun onDificultadChange(valor: Dificultad) =
        _form.update { it.copy(dificultad = if (it.dificultad == valor) null else valor) }

    fun limpiarFiltros() =
        _form.update { it.copy(modulo = null, curso = null, dificultad = null) }

    // ---------- Asignar actividad ----------

    fun abrirAsignacion(actividad: Actividad) =
        _form.update {
            it.copy(
                actividadSeleccionada = actividad,
                alias = "",
                errorAlias = null,
                codigoGenerado = null
            )
        }

    fun onAliasChange(valor: String) =
        _form.update { it.copy(alias = valor.take(MAX_ALIAS), errorAlias = null) }

    fun confirmarAsignacion() {
        val estado = _form.value
        val actividad = estado.actividadSeleccionada ?: return

        val error = validarAlias(estado.alias)
        if (error != null) {
            _form.update { it.copy(errorAlias = error) }
            return
        }

        val asignacion = RepositorioActividades.crearAsignacion(
            actividad = actividad,
            aliasEstudiante = estado.alias,
            asistente = CuentasDePrueba.ASISTENTE_ALIAS
        )
        _form.update { it.copy(codigoGenerado = asignacion.codigo) }
    }

    fun cerrarAsignacion() =
        _form.update {
            it.copy(actividadSeleccionada = null, alias = "", errorAlias = null, codigoGenerado = null)
        }

    private fun validarAlias(alias: String): String? = when {
        alias.isBlank() -> "Escribe un alias ficticio para el estudiante"
        alias.trim().length < 2 -> "El alias es muy corto"
        !REGEX_ALIAS.matches(alias) -> "Usa solo letras y números (sin símbolos)"
        else -> null
    }

    // ---------- Observaciones pedagógicas ----------

    fun abrirObservacion(asignacion: Asignacion) =
        _form.update {
            it.copy(
                asignacionEnObservacion = asignacion,
                textoObservacion = asignacion.observacion,
                errorObservacion = null
            )
        }

    fun onObservacionChange(valor: String) =
        _form.update { it.copy(textoObservacion = valor.take(MAX_OBSERVACION), errorObservacion = null) }

    fun guardarObservacion() {
        val estado = _form.value
        val asignacion = estado.asignacionEnObservacion ?: return

        if (estado.textoObservacion.isBlank()) {
            _form.update { it.copy(errorObservacion = "Escribe una observación general") }
            return
        }
        RepositorioActividades.registrarObservacion(asignacion.id, estado.textoObservacion)
        cerrarObservacion()
    }

    fun cerrarObservacion() =
        _form.update {
            it.copy(asignacionEnObservacion = null, textoObservacion = "", errorObservacion = null)
        }
}
