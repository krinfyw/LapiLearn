package com.example.lapilearn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "docentes")
data class DocenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correo: String,
    val contrasena: String,
    val nombre: String = ""
)