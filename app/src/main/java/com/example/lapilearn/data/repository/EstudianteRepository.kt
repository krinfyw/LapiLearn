package com.example.lapilearn.data.repository

import android.content.Context
import com.example.lapilearn.data.local.AppDatabase
import androidx.room.withTransaction
import com.example.lapilearn.data.local.entity.*
import com.example.lapilearn.model.SesionEstudiante
import java.util.Locale

class EstudianteRepository(private val db: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getInstance(context.applicationContext))

    private val dao = db.estudianteDao()

    // Reingresar el código recupera el último intento; no gasta otro.
    suspend fun buscarSesion(codigo: String): SesionEstudiante = db.withTransaction {
        val normalizado = codigo.trim().uppercase(Locale.ROOT)
        require(normalizado.isNotBlank()) { "Ingresa el código de tu actividad." }
        val asignacion = requireNotNull(
            db.asignacionDao().porCodigo(normalizado)
        ) { "No encontramos ese código. Revísalo con tu asistente." }
        val actividad = requireNotNull(
            db.actividadDao().porId(asignacion.actividadId)
        ) { "La actividad asignada ya no existe." }
        validarActividad(actividad)
        val ultimo = dao.ultimoIntentoDeAsignacion(asignacion.id)
        val id = ultimo?.id ?: dao.insertarIntento(
            IntentoEntity(
                actividadId = actividad.id, asignacionId = asignacion.id, numero = 1,
                iniciadoEn = System.currentTimeMillis()
            )
        )
        cargar(id)
    }

    private suspend fun validarActividad(actividad: ActividadEntity) {
        require(actividad.estado == "Disponible") { "Esta actividad todavía no está disponible. Consulta a tu asistente." }
        require(
            dao.preguntas(actividad.id).isNotEmpty()
        ) { "Esta actividad aún no tiene preguntas preparadas." }
        require(actividad.intentosPermitidos > 0) { "Esta actividad no tiene intentos disponibles." }
    }

    private suspend fun cargar(id: Long): SesionEstudiante {
        val intento =
            requireNotNull(dao.intento(id)) { "No encontramos la sesión. Ingresa tu código nuevamente." }
        val actividad = requireNotNull(db.actividadDao().porId(intento.actividadId))
        return SesionEstudiante(actividad, dao.preguntas(actividad.id), intento, dao.respuestas(id))
    }

    suspend fun guardarRespuesta(
        intentoId: Long,
        preguntaId: Long,
        alternativa: Int
    ): SesionEstudiante = db.withTransaction {
        val sesion = cargar(intentoId)
        validarActividad(sesion.actividad)
        require(!sesion.finalizada) { "El intento ya terminó." }
        val siguiente =
            sesion.preguntas.firstOrNull { p -> sesion.respuestas.none { it.preguntaId == p.id } }
        require(siguiente != null && siguiente.id == preguntaId) { "Esta pregunta ya fue respondida o no corresponde al paso actual." }
        require(alternativa in siguiente.alternativas.indices) { "Selecciona una alternativa válida." }
        dao.insertarRespuesta(
            RespuestaEntity(
                intentoId, preguntaId, alternativa,
                alternativa == siguiente.correcta, System.currentTimeMillis()
            )
        )
        if (sesion.respuestas.size + 1 == sesion.preguntas.size) dao.finalizar(
            intentoId,
            System.currentTimeMillis()
        )
        cargar(intentoId)
    }

    suspend fun crearReintento(intentoId: Long): SesionEstudiante = db.withTransaction {
        val anterior = cargar(intentoId)
        validarActividad(anterior.actividad)
        require(anterior.finalizada) { "Primero termina el intento actual." }
        val asignacionId = anterior.intento.asignacionId
        val ultimo = requireNotNull(
            if (asignacionId != null) dao.ultimoIntentoDeAsignacion(asignacionId)
            else dao.ultimoIntento(anterior.actividad.id)
        )
        if (ultimo.id != intentoId) return@withTransaction cargar(ultimo.id)
        require(ultimo.numero < anterior.actividad.intentosPermitidos) { "Ya utilizaste los intentos permitidos. Consulta a tu asistente." }
        val id = dao.insertarIntento(
            IntentoEntity(
                actividadId = anterior.actividad.id,
                asignacionId = asignacionId,
                numero = ultimo.numero + 1, iniciadoEn = System.currentTimeMillis()
            )
        )
        cargar(id)
    }
}
