package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lapilearn.model.Actividad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val modulo: String,
    val curso: String,
    val dificultad: String,
    val objetivo: String,
    val intentosPermitidos: Int,
    val estado: String,
    val instrucciones: String
) {
    fun toModel() = Actividad(
        id,
        titulo,
        modulo,
        curso,
        dificultad,
        objetivo,
        intentosPermitidos.toString(),
        estado,
        instrucciones
    )
}
