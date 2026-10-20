package com.example.lapilearn.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lapilearn.data.local.entity.AsignacionEntity
import com.example.lapilearn.data.local.entity.IntentoEntity
import com.example.lapilearn.data.local.entity.RespuestaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignacionDao {
    @Query("SELECT * FROM asignaciones ORDER BY id DESC")
    fun observarTodas(): Flow<List<AsignacionEntity>>

    @Query("SELECT * FROM asignaciones WHERE codigo = :codigo LIMIT 1")
    suspend fun porCodigo(codigo: String): AsignacionEntity?

    @Query("SELECT COUNT(*) FROM asignaciones WHERE codigo = :codigo")
    suspend fun contarCodigo(codigo: String): Int

    @Insert
    suspend fun insertar(asignacion: AsignacionEntity): Long

    @Query("UPDATE asignaciones SET observacion = :texto WHERE id = :id")
    suspend fun actualizarObservacion(id: Long, texto: String)

    // Para el resumen de la asistente: intentos y respuestas ligados a una asignación.
    @Query("SELECT * FROM intentos WHERE asignacionId IS NOT NULL")
    fun observarIntentos(): Flow<List<IntentoEntity>>

    @Query("SELECT r.* FROM respuestas r INNER JOIN intentos i ON i.id = r.intentoId WHERE i.asignacionId IS NOT NULL")
    fun observarRespuestas(): Flow<List<RespuestaEntity>>
}
