package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "intentos", foreignKeys = [ForeignKey(
    entity = ActividadEntity::class, parentColumns = ["id"], childColumns = ["actividadId"],
    onDelete = ForeignKey.RESTRICT
)], indices = [Index(value = ["actividadId", "numero"], unique = true)])
data class IntentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Int,
    val numero: Int,
    val iniciadoEn: Long,
    val finalizadoEn: Long? = null
)
