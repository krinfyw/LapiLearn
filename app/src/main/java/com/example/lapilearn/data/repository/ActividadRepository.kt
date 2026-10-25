package com.example.lapilearn.data.repository

import android.content.Context
import com.example.lapilearn.data.local.AppDatabase
import androidx.room.withTransaction
import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.data.local.entity.PreguntaEntity
import com.example.lapilearn.model.Actividad
import kotlinx.coroutines.flow.map

class ActividadRepository(private val db: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getInstance(context.applicationContext))

    private val dao = db.actividadDao()
    private val daoPreguntas = db.estudianteDao()

    val actividades = dao.observarTodas().map { lista -> lista.map { it.toModel() } }

    suspend fun porId(id: Int): Actividad? = dao.porId(id)?.toModel()

    suspend fun preguntas(actividadId: Int) = daoPreguntas.preguntas(actividadId)

    suspend fun agregarPregunta(pregunta: PreguntaEntity) = db.withTransaction {
        val orden = daoPreguntas.ultimoOrdenPregunta(pregunta.actividadId) + 1
        daoPreguntas.insertarPregunta(pregunta.copy(orden = orden))
    }

    suspend fun actualizarPregunta(pregunta: PreguntaEntity) =
        daoPreguntas.actualizarPregunta(pregunta)

    private fun intentos(valor: String): Int {
        return valor.toIntOrNull()?.takeIf {
            it > 0
        } ?: throw IllegalArgumentException(
            "Ingresa un número de intentos mayor que cero."
        )
    }

    // GUARDAR ACTIVIDAD EN ROOM
    suspend fun agregar(actividad: Actividad): Long {
        require(actividad.titulo.isNotBlank()) {
            "El título es obligatorio."
        }
        require(actividad.objetivo.isNotBlank()) {
            "El objetivo es obligatorio."
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
                instrucciones = actividad.instrucciones
            )
        )
    }

    suspend fun actualizar(
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
        if (actividad.estado == "Disponible") {
            require(daoPreguntas.preguntas(actividad.id).isNotEmpty()) {
                "Agrega al menos una pregunta antes de dejar la actividad disponible."
            }
        }
        dao.actualizar(
            original.copy(
                titulo = actividad.titulo,
                modulo = actividad.modulo,
                curso = actividad.curso,
                dificultad = actividad.dificultad,
                objetivo = actividad.objetivo.trim(),
                intentosPermitidos = intentos(actividad.intentos),
                instrucciones = actividad.instrucciones,
                estado = actividad.estado
            )
        )
    }

}
