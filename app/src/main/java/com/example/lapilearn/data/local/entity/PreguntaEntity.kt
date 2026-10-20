package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Cuatro alternativas por pregunta para este MVP. La respuesta correcta nunca viene de la UI.
@Entity(tableName = "preguntas", foreignKeys = [ForeignKey(
    entity = ActividadEntity::class, parentColumns = ["id"], childColumns = ["actividadId"],
    onDelete = ForeignKey.RESTRICT
)], indices = [Index(value = ["actividadId", "orden"], unique = true)])
data class PreguntaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Int,
    val orden: Int,
    val textoApoyo: String,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val opcionD: String,
    val correcta: Int,
    val retroalimentacion: String
) {
    val alternativas: List<String> get() = listOf(opcionA, opcionB, opcionC, opcionD)
}
