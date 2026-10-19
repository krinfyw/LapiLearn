package com.example.lapilearn.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lapilearn.model.Actividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActividadViewModel : ViewModel() {

    private val _actividades = MutableStateFlow(
        listOf(
            Actividad(1, "El bosque encantado", "Lectura", "2° básico", "Media", "Comprensión implícita", "2", "Disponible"),
            Actividad(2, "Sumas hasta 100", "Matemática", "2° básico", "Media", "Operatoria", "2", "Borrador"),
            Actividad(3, "Vocabulario: animales", "Lectura", "1° básico", "Baja", "Vocabulario", "3", "Disponible")
        )
    )
    val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()
    var dificultadSeleccionada by mutableStateOf("")

    private var idSeleccionada: Int? = null

    fun seleccionar(id: Int) {
        idSeleccionada = id
    }

    fun obtenerSeleccionada(): Actividad? {
        return _actividades.value.find { it.id == idSeleccionada }
    }

    fun actualizar(actividad: Actividad) {
        _actividades.update { lista ->
            lista.map { if (it.id == actividad.id) actividad else it }
        }
    }
}