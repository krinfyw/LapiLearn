package com.example.lapilearn.data.repository

import android.content.Context
import com.example.lapilearn.data.local.AppDatabase
import androidx.room.withTransaction
import com.example.lapilearn.data.local.entity.AsignacionEntity
import com.example.lapilearn.utils.AsignacionReglas

class AsignacionRepository(private val db: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getInstance(context.applicationContext))

    private val asignacionDao = db.asignacionDao()

    val actividades = db.actividadDao().observarTodas()
    val asignaciones = asignacionDao.observarTodas()
    val intentos = asignacionDao.observarIntentos()
    val respuestas = asignacionDao.observarRespuestas()

    suspend fun crearAsignacion(actividadId: Int, alias: String, responsable: String): String =
        db.withTransaction {
            val vigente = requireNotNull(
                db.actividadDao().porId(actividadId)
            ) { "La actividad ya no existe." }
            require(vigente.estado == "Disponible") { "Esta actividad ya no está disponible." }
            require(db.estudianteDao().preguntas(vigente.id).isNotEmpty()) {
                "Esta actividad aún no tiene preguntas. Avisa a la docente o UTP."
            }

            var candidato = AsignacionReglas.generarCodigo()
            while (asignacionDao.contarCodigo(candidato) > 0) {
                candidato = AsignacionReglas.generarCodigo()
            }

            asignacionDao.insertar(
                AsignacionEntity(
                    aliasEstudiante = alias.trim(),
                    actividadId = vigente.id,
                    codigo = candidato,
                    asistente = responsable,
                    creadaEn = System.currentTimeMillis()
                )
            )
            candidato
        }

    suspend fun actualizarObservacion(id: Long, texto: String) =
        asignacionDao.actualizarObservacion(id, texto)
}
