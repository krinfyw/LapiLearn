package com.example.lapilearn.data.local.dao

import androidx.room.*
import com.example.lapilearn.data.local.entity.*

@Dao
interface EstudianteDao {
    @Query("SELECT * FROM preguntas WHERE actividadId = :actividadId ORDER BY orden")
    suspend fun preguntas(actividadId: Int): List<PreguntaEntity>

    @Query("SELECT * FROM intentos WHERE actividadId = :actividadId ORDER BY numero DESC LIMIT 1")
    suspend fun ultimoIntento(actividadId: Int): IntentoEntity?

    @Query("SELECT * FROM intentos WHERE asignacionId = :asignacionId ORDER BY numero DESC LIMIT 1")
    suspend fun ultimoIntentoDeAsignacion(asignacionId: Long): IntentoEntity?

    @Query("SELECT * FROM intentos WHERE id = :id")
    suspend fun intento(id: Long): IntentoEntity?

    @Query("SELECT * FROM respuestas WHERE intentoId = :intentoId")
    suspend fun respuestas(intentoId: Long): List<RespuestaEntity>

    @Insert
    suspend fun insertarIntento(intento: IntentoEntity): Long

    @Insert
    suspend fun insertarRespuesta(respuesta: RespuestaEntity)

    @Query("UPDATE intentos SET finalizadoEn = :fecha WHERE id = :id AND finalizadoEn IS NULL")
    suspend fun finalizar(id: Long, fecha: Long)

    @Insert
    suspend fun insertarPregunta(pregunta: PreguntaEntity)

    @Update
    suspend fun actualizarPregunta(pregunta: PreguntaEntity)

    @Query("SELECT COALESCE(MAX(orden), 0) FROM preguntas WHERE actividadId = :actividadId")
    suspend fun ultimoOrdenPregunta(actividadId: Int): Int
}
