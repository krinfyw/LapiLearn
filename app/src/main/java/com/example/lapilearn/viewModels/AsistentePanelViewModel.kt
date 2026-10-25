package com.example.lapilearn.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lapilearn.data.repository.AsignacionRepository
import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.model.AsistentePanelUiState
import com.example.lapilearn.model.ResumenAsignacion
import com.example.lapilearn.utils.AsignacionReglas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Panel de la asistente de aula: filtra el catálogo, asigna actividades a un estudiante
 * ficticio (código temporal) y muestra resultados básicos y observaciones.
 * Todo viene de Room, la misma base que usan docente y estudiante.
 */
class AsistentePanelViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val MODULOS = listOf("Lectura", "Matemática")
        val CURSOS = listOf("1° básico", "2° básico", "3° básico", "4° básico")
        val DIFICULTADES = listOf("Fácil", "Media", "Avanzada")
    }

    private val repository = AsignacionRepository(application)

    // Estado de filtros y diálogos. Los datos se combinan desde Room.
    private val _form = MutableStateFlow(AsistentePanelUiState())

    val uiState: StateFlow<AsistentePanelUiState> = combine(
        _form,
        repository.actividades,
        repository.asignaciones,
        repository.intentos,
        repository.respuestas
    ) { form, actividades, asignaciones, intentos, respuestas ->
        form.copy(
            actividades = filtrar(actividades, form),
            asignaciones = AsignacionReglas.resumir(asignaciones, actividades, intentos, respuestas)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AsistentePanelUiState())

    // Solo se pueden asignar actividades autorizadas (estado "Disponible").
    private fun filtrar(
        actividades: List<ActividadEntity>,
        form: AsistentePanelUiState
    ): List<ActividadEntity> =
        actividades
            .filter { it.estado == "Disponible" }
            .filter { form.modulo == null || it.modulo == form.modulo }
            .filter { form.curso == null || it.curso == form.curso }
            .filter { form.dificultad == null || it.dificultad == form.dificultad }
            .sortedWith(compareBy({ it.curso }, { it.titulo }))

    // ---------- Filtros (tocar de nuevo el mismo filtro lo quita) ----------

    fun onModuloChange(valor: String) =
        _form.update { it.copy(modulo = if (it.modulo == valor) null else valor) }

    fun onCursoChange(valor: String) =
        _form.update { it.copy(curso = if (it.curso == valor) null else valor) }

    fun onDificultadChange(valor: String) =
        _form.update { it.copy(dificultad = if (it.dificultad == valor) null else valor) }

    fun limpiarFiltros() =
        _form.update { it.copy(modulo = null, curso = null, dificultad = null) }

    // ---------- Asignar actividad ----------

    fun abrirAsignacion(actividad: ActividadEntity) =
        _form.update {
            it.copy(
                actividadSeleccionada = actividad,
                alias = "",
                errorAlias = null,
                guardando = false,
                codigoGenerado = null
            )
        }

    fun onAliasChange(valor: String) =
        _form.update { it.copy(alias = valor.take(AsignacionReglas.MAX_ALIAS), errorAlias = null) }

    fun confirmarAsignacion(responsable: String) {
        val form = _form.value
        val actividad = form.actividadSeleccionada ?: return
        if (form.guardando || form.codigoGenerado != null) return

        val error = AsignacionReglas.validarAlias(form.alias)
        if (error != null) {
            _form.update { it.copy(errorAlias = error) }
            return
        }

        _form.update { it.copy(guardando = true, errorAlias = null) }
        viewModelScope.launch {
            try {
                val codigo = repository.crearAsignacion(actividad.id, form.alias, responsable)
                _form.update { it.copy(codigoGenerado = codigo, guardando = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val mensaje = if (e is IllegalArgumentException) {
                    e.message ?: "Revisa los datos."
                } else {
                    "No se pudo crear la asignación. Intenta nuevamente."
                }
                _form.update { it.copy(errorAlias = mensaje, guardando = false) }
            }
        }
    }

    fun cerrarAsignacion() =
        _form.update {
            it.copy(
                actividadSeleccionada = null,
                alias = "",
                errorAlias = null,
                guardando = false,
                codigoGenerado = null
            )
        }

    // ---------- Observaciones pedagógicas generales (ficticias) ----------

    fun abrirObservacion(resumen: ResumenAsignacion) =
        _form.update {
            it.copy(
                asignacionEnObservacion = resumen,
                textoObservacion = resumen.asignacion.observacion,
                errorObservacion = null
            )
        }

    fun onObservacionChange(valor: String) =
        _form.update {
            it.copy(
                textoObservacion = valor.take(AsignacionReglas.MAX_OBSERVACION),
                errorObservacion = null
            )
        }

    fun guardarObservacion() {
        val form = _form.value
        val resumen = form.asignacionEnObservacion ?: return

        val error = AsignacionReglas.validarObservacion(form.textoObservacion)
        if (error != null) {
            _form.update { it.copy(errorObservacion = error) }
            return
        }

        viewModelScope.launch {
            try {
                repository.actualizarObservacion(
                    resumen.asignacion.id,
                    form.textoObservacion.trim()
                )
                cerrarObservacion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _form.update { it.copy(errorObservacion = "No se pudo guardar. Intenta nuevamente.") }
            }
        }
    }

    fun cerrarObservacion() =
        _form.update {
            it.copy(asignacionEnObservacion = null, textoObservacion = "", errorObservacion = null)
        }
}
