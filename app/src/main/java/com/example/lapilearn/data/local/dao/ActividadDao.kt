package com.example.lapilearn.data.local.dao

import androidx.room.*
import com.example.lapilearn.data.local.entity.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY id")
    fun observarTodas(): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE codigo = :codigo LIMIT 1")
    suspend fun porCodigo(codigo: String): ActividadEntity?

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun porId(id: Int): ActividadEntity?

    @Insert
    suspend fun insertar(actividad: ActividadEntity): Long

    @Update
    suspend fun actualizar(actividad: ActividadEntity)
}
