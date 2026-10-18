package com.example.lapilearn.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lapilearn.data.local.entity.AsistenteEntity

@Dao
interface AsistenteDao {
    @Query("SELECT * FROM asistentes WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun login(correo: String, contrasena: String): AsistenteEntity?

    @Insert
    suspend fun insert(asistente: AsistenteEntity)
}