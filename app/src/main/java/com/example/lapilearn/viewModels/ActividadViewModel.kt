package com.example.lapilearn.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lapilearn.data.repository.ActividadRepository
import com.example.lapilearn.data.local.entity.PreguntaEntity
import com.example.lapilearn.model.Actividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Room es la fuente de datos del catálogo.
class ActividadViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ActividadRepository(application)
    var preguntasGuardadas by mutableStateOf<List<PreguntaEntity>>(emptyList())
        private set

    val actividades = repository.actividades
        .stateIn( //Mantiene en memoria el último valor emitido, haciendo que multiples pantallas puedan ver el mismo StateFlow en vez de ejecutar la misma consulta para cada una
            viewModelScope, //Define el contexto en el se ejecutará la recoleción de los datos de la base de datos  en segundo plano
            SharingStarted.Eagerly, //Compienza de inmediato a recolectar y transformar los datos, sin esperar a que se dibuje la pantalla
            emptyList()
        )
    var errorGuardado by mutableStateOf<String?>(null)
        private set
    var guardandoActividad by mutableStateOf(false)
        private set
    var guardandoPregunta by mutableStateOf(false)
        private set
    private var idSeleccionada: Int? = null
    private var actividadNueva: Actividad? = null

    var preguntaSeleccionada by mutableStateOf<PreguntaEntity?>(null)

    // SELECCIONAR ACTIVIDAD
    fun seleccionar(id: Int) {
        idSeleccionada = id
    }

    fun seleccionarPregunta(pregunta: PreguntaEntity) {
        preguntaSeleccionada = pregunta
    }

    fun obtenerSeleccionada(): Actividad? {
        val actividad = actividades.value.find {
            it.id == idSeleccionada
        }
        if (actividad != null) {
            return actividad
        }
        if (actividadNueva?.id == idSeleccionada) {
            return actividadNueva
        }
        return null
    }

    suspend fun obtenerActividadPorId(): Actividad? {
        val id = idSeleccionada ?: return null
        return repository.porId(id)
    }

    fun consultarPreguntas() {
        viewModelScope.launch {
            val actividad = obtenerActividadPorId()
            if (actividad != null) {
                preguntasGuardadas = repository.preguntas(actividad.id)
            }
        }
    }

    fun actualizar(actividad: Actividad, onGuardada: () -> Unit) = guardar(onGuardada) {
        repository.actualizar(actividad)
    }

    // ROOM GENERA EL ID DEFINITIVO
    fun nuevoId(): Int = 0

    // AGREGAR ACTIVIDAD NUEVA
    fun agregar(
        actividad: Actividad,
        onGuardada: () -> Unit
    ) {
        viewModelScope.launch {
            errorGuardado = null
            try {
                val id = repository.agregar(actividad)
                seleccionar(id.toInt())

                actividadNueva = actividad.copy(
                    id = id.toInt(),
                    estado = "Borrador"
                )
                onGuardada()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo guardar la actividad."
            }
        }
    }

    // GUARDAR CAMBIOS
    private fun guardar(onGuardada: () -> Unit, operacion: suspend () -> Unit) {
        if (guardandoActividad) return
        guardandoActividad = true
        errorGuardado = null
        viewModelScope.launch {
            try {
                operacion()
                onGuardada()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo guardar la actividad."
            } finally {
                guardandoActividad = false
            }
        }
    }

    fun agregarPregunta(
        enunciado: String,
        opcionA: String,
        opcionB: String,
        opcionC: String,
        opcionD: String,
        correcta: Int,
        textoApoyo: String,
        retroalimentacion: String,
        onGuardada: () -> Unit
    ) {
        if (guardandoPregunta) return
        guardandoPregunta = true
        errorGuardado = null
        viewModelScope.launch {
            try {
                val actividad = obtenerActividadPorId()
                    ?: throw IllegalArgumentException(
                        "Primero selecciona una actividad."
                    )
                require(enunciado.isNotBlank()) {
                    "La pregunta es obligatoria."
                }
                require(opcionA.isNotBlank()) {
                    "La alternativa A es obligatoria."
                }
                require(opcionB.isNotBlank()) {
                    "La alternativa B es obligatoria."
                }
                require(opcionC.isNotBlank()) {
                    "La alternativa C es obligatoria."
                }
                require(opcionD.isNotBlank()) {
                    "La alternativa D es obligatoria."
                }
                require(correcta in 0..3) {
                    "Selecciona una respuesta correcta."
                }
                require(retroalimentacion.isNotBlank()) {
                    "Escribe la retroalimentación."
                }
                repository.agregarPregunta(
                    PreguntaEntity(
                        actividadId = actividad.id,
                        orden = 0,
                        textoApoyo = textoApoyo,
                        enunciado = enunciado,
                        opcionA = opcionA,
                        opcionB = opcionB,
                        opcionC = opcionC,
                        opcionD = opcionD,
                        correcta = correcta,
                        retroalimentacion = retroalimentacion
                    )
                )
                onGuardada()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo guardar la pregunta."
            } finally {
                guardandoPregunta = false
            }
        }
    }

    fun actualizarPregunta(
        enunciado: String,
        opcionA: String,
        opcionB: String,
        opcionC: String,
        opcionD: String,
        correcta: Int,
        textoApoyo: String,
        retroalimentacion: String,
        onGuardada: () -> Unit
    ) {
        viewModelScope.launch {
            errorGuardado = null
            try {
                val pregunta = preguntaSeleccionada
                require(enunciado.isNotBlank()) {
                    "La pregunta es obligatoria."
                }
                require(opcionA.isNotBlank()) {
                    "La alternativa A es obligatoria."
                }
                require(opcionB.isNotBlank()) {
                    "La alternativa B es obligatoria."
                }
                require(opcionC.isNotBlank()) {
                    "La alternativa C es obligatoria."
                }
                require(opcionD.isNotBlank()) {
                    "La alternativa D es obligatoria."
                }
                require(correcta in 0..3) {
                    "Selecciona una respuesta correcta."
                }
                require(retroalimentacion.isNotBlank()) {
                    "Escribe la retroalimentación."
                }
                if (pregunta != null) {
                    repository.actualizarPregunta(
                        pregunta.copy(
                            enunciado = enunciado,
                            opcionA = opcionA,
                            opcionB = opcionB,
                            opcionC = opcionC,
                            opcionD = opcionD,
                            correcta = correcta,
                            textoApoyo = textoApoyo,
                            retroalimentacion = retroalimentacion
                        )
                    )
                    consultarPreguntas()
                    preguntaSeleccionada = null
                    onGuardada()
                } else {
                    errorGuardado = "Completa todos los campos de la pregunta."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo actualizar la pregunta."
            }
        }
    }
}
