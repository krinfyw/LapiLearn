package com.example.lapilearn.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.lapilearn.data.local.entity.ActividadEntity
import java.util.UUID
import com.example.lapilearn.data.local.AppDatabase
import com.example.lapilearn.model.Actividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Mantiene la API del catálogo; Room es ahora la fuente de datos.
class ActividadViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.actividadDao()
    val actividades = dao.observarTodas()
        .map { lista -> lista.map { it.toModel() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    var dificultadSeleccionada by mutableStateOf("")
    var errorGuardado by mutableStateOf<String?>(null)
        private set
    private var idSeleccionada: Int? = null
    fun seleccionar(id: Int) { idSeleccionada = id }
    fun obtenerSeleccionada(): Actividad? = actividades.value.find { it.id == idSeleccionada }
    fun actualizar(actividad: Actividad) = guardar { actualizarEnRoom(actividad) }
    fun agregar(actividad: Actividad) = guardar { agregarEnRoom(actividad) }
    // Compatibilidad con NuevaActividadScreen: Room genera el identificador definitivo.
    fun nuevoId(): Int = 0

    private fun guardar(operacion: suspend () -> Unit) {
        viewModelScope.launch {
            errorGuardado = null
            try { operacion() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { errorGuardado = e.message ?: "No se pudo guardar la actividad." }
        }
    }

    private fun intentos(valor: String): Int = valor.toIntOrNull()?.takeIf { it > 0 }
        ?: throw IllegalArgumentException("Los intentos deben ser un número entero mayor que cero.")

    private suspend fun agregarEnRoom(actividad: Actividad) {
        require(actividad.titulo.isNotBlank() && actividad.objetivo.isNotBlank()) { "Completa título y objetivo." }
        dao.insertar(ActividadEntity(
            titulo = actividad.titulo.trim(), modulo = actividad.modulo, curso = actividad.curso,
            dificultad = actividad.dificultad, objetivo = actividad.objetivo.trim(),
            intentosPermitidos = intentos(actividad.intentos), estado = actividad.estado,
            codigo = UUID.randomUUID().toString().replace("-", "").uppercase(),
            instrucciones = "Lee y confirma cada respuesta."
        ))
        // Las actividades nuevas necesitan preguntas antes de permitir el acceso.
    }

    private suspend fun actualizarEnRoom(actividad: Actividad) = db.withTransaction {
        require(actividad.objetivo.isNotBlank()) { "El objetivo es obligatorio." }
        val original = requireNotNull(dao.porId(actividad.id)) { "La actividad ya no existe." }
        dao.actualizar(original.copy(
            titulo = actividad.titulo, modulo = actividad.modulo, curso = actividad.curso,
            dificultad = actividad.dificultad, objetivo = actividad.objetivo.trim(),
            intentosPermitidos = intentos(actividad.intentos), estado = actividad.estado
        ))
    }
}
