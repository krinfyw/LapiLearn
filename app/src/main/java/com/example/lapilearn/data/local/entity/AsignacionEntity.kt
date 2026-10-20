package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Actividad asignada por una asistente a un estudiante FICTICIO (solo un alias).
 * El estudiante entra con [codigo] (temporal y único por asignación).
 * El estado (pendiente / en desarrollo / finalizada), los intentos y las respuestas
 * se obtienen de la tabla `intentos` (columna asignacionId), no se guardan aquí.
 */
@Entity(
    tableName = "asignaciones",
    foreignKeys = [ForeignKey(
        entity = ActividadEntity::class, parentColumns = ["id"], childColumns = ["actividadId"],
        onDelete = ForeignKey.RESTRICT
    )],
    indices = [Index(value = ["codigo"], unique = true), Index("actividadId")]
)
data class AsignacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val aliasEstudiante: String,
    val actividadId: Int,
    val codigo: String,
    val asistente: String,
    val creadaEn: Long,
    val observacion: String = ""
)
