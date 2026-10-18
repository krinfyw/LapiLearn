package com.example.lapilearn.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lapilearn.data.local.entity.DocenteEntity

@Dao
interface DocenteDao {
    @Query("SELECT * FROM docentes WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun login(correo: String, contrasena: String): DocenteEntity?

    @Insert
    suspend fun insert(docente: DocenteEntity)
}