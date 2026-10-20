package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Cada intento pertenece a una asignación (estudiante ficticio + actividad).
// El número de intento es único dentro de la asignación, no dentro de la actividad,
// así dos estudiantes distintos pueden hacer la misma actividad.
@Entity(tableName = "intentos", foreignKeys = [
    ForeignKey(
        entity = ActividadEntity::class, parentColumns = ["id"], childColumns = ["actividadId"],
        onDelete = ForeignKey.RESTRICT
    ),
    ForeignKey(
        entity = AsignacionEntity::class, parentColumns = ["id"], childColumns = ["asignacionId"],
        onDelete = ForeignKey.RESTRICT
    )
], indices = [Index("actividadId"), Index(value = ["asignacionId", "numero"], unique = true)])
data class IntentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Int,
    val numero: Int,
    val iniciadoEn: Long,
    val finalizadoEn: Long? = null,
    val asignacionId: Long? = null
)
