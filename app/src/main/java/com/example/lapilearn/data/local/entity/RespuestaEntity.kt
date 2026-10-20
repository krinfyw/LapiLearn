package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "respuestas", primaryKeys = ["intentoId", "preguntaId"], foreignKeys = [
    ForeignKey(entity = IntentoEntity::class, parentColumns = ["id"], childColumns = ["intentoId"], onDelete = ForeignKey.RESTRICT),
    ForeignKey(entity = PreguntaEntity::class, parentColumns = ["id"], childColumns = ["preguntaId"], onDelete = ForeignKey.RESTRICT)
], indices = [Index("preguntaId")])
data class RespuestaEntity(
    val intentoId: Long,
    val preguntaId: Long,
    val alternativa: Int,
    val esCorrecta: Boolean,
    val respondidaEn: Long
)
