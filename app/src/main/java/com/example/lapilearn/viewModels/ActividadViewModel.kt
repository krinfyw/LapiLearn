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
import com.example.lapilearn.data.local.entity.PreguntaEntity
import com.example.lapilearn.model.Actividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Room es la fuente de datos del catálogo.
class ActividadViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.actividadDao()
    private val daoPreguntas = db.estudianteDao()
    val actividades = dao.observarTodas()
        .map { lista -> lista.map { it.toModel() } }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
    var errorGuardado by mutableStateOf<String?>(null)
        private set
    private var idSeleccionada: Int? = null
    private var actividadNueva: Actividad? = null

    // SELECCIONAR ACTIVIDAD
    fun seleccionar(id: Int) {
        idSeleccionada = id
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

    // BUSCAR ACTIVIDAD EN ROOM
    suspend fun obtenerActividadPorId(): Actividad? {
        val id = idSeleccionada ?: return null
        return dao.porId(id)?.toModel()
    }

    // ACTUALIZAR ACTIVIDAD
    fun actualizar(actividad: Actividad) = guardar {
        actualizarEnRoom(actividad)
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
                val id = agregarEnRoom(actividad)
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
    private fun guardar(operacion: suspend () -> Unit) {
        viewModelScope.launch {
            errorGuardado = null
            try {
                operacion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo guardar la actividad."
            }
        }
    }

    // VALIDAR INTENTOS
    private fun intentos(valor: String): Int {
        return valor.toIntOrNull()?.takeIf {
            it > 0
        } ?: throw IllegalArgumentException(
            "Los intentos deben ser un número entero mayor que cero."
        )
    }

    // GUARDAR ACTIVIDAD EN ROOM
    private suspend fun agregarEnRoom(actividad: Actividad): Long {
        require(
            actividad.titulo.isNotBlank() &&
                    actividad.objetivo.isNotBlank()
        ) {
            "Completa título y objetivo."
        }
        return dao.insertar(
            ActividadEntity(
                titulo = actividad.titulo.trim(),
                modulo = actividad.modulo,
                curso = actividad.curso,
                dificultad = actividad.dificultad,
                objetivo = actividad.objetivo.trim(),
                intentosPermitidos = intentos(actividad.intentos),
                estado = "Borrador",
                codigo = UUID.randomUUID().toString()
                    .replace("-", "")
                    .uppercase(),
                instrucciones = "Lee y confirma cada respuesta."
            )
        )
    }

    // ACTUALIZAR ACTIVIDAD EN ROOM
    private suspend fun actualizarEnRoom(
        actividad: Actividad
    ) = db.withTransaction {
    require(actividad.objetivo.isNotBlank()) {
            "El objetivo es obligatorio."
        }
        val original = requireNotNull(
            dao.porId(actividad.id)
        ) {
            "La actividad ya no existe."
        }
        dao.actualizar(
            original.copy(
                titulo = actividad.titulo,
                modulo = actividad.modulo,
                curso = actividad.curso,
                dificultad = actividad.dificultad,
                objetivo = actividad.objetivo.trim(),
                intentosPermitidos = intentos(actividad.intentos),
                estado = actividad.estado
            )
        )
    }

    // AGREGAR PREGUNTA
    fun agregarPregunta(
        enunciado: String,
        opcionA: String,
        opcionB: String,
        opcionC: String,
        opcionD: String,
        correcta: Int,
        textoApoyo: String,
        retroalimentacion: String
    ) {
        viewModelScope.launch {
            errorGuardado = null
            try {
                val actividad = obtenerActividadPorId()
                    ?: throw IllegalArgumentException(
                        "Primero selecciona una actividad."
                    )
                require(
                    enunciado.isNotBlank() &&
                            opcionA.isNotBlank() &&
                            opcionB.isNotBlank() &&
                            opcionC.isNotBlank() &&
                            opcionD.isNotBlank()
                ) {
                    "Completa la pregunta y las cuatro alternativas."
                }
                require(correcta in 0..3) {
                    "Selecciona una respuesta correcta."
                }
                require(retroalimentacion.isNotBlank()) {
                    "Escribe la retroalimentación."
                }
                db.withTransaction {
                    val orden = daoPreguntas.ultimoOrdenPregunta(
                        actividad.id
                    ) + 1
                    daoPreguntas.insertarPregunta(
                        PreguntaEntity(
                            actividadId = actividad.id,
                            orden = orden,
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
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGuardado = e.message
                    ?: "No se pudo guardar la pregunta."
            }
        }
    }
}
