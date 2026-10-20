package com.example.lapilearn.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.lapilearn.data.local.entity.*
import com.example.lapilearn.data.local.AppDatabase
import com.example.lapilearn.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

// Como en My Tasks: el ViewModel consulta los DAO y expone el estado a Compose.
class EstudianteViewModel(
    application: Application,
    private val db: AppDatabase
) : AndroidViewModel(application) {
    // Android usa este constructor; las pruebas pueden proporcionar una base aislada.
    constructor(application: Application) : this(application, AppDatabase.getInstance(application))

    private val dao = db.estudianteDao()
    private val _uiState = MutableStateFlow(EstudianteUiState())
    val uiState = _uiState.asStateFlow()

    fun onCodigoChange(valor: String) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(codigo = valor.uppercase(Locale.ROOT), errores = EstudianteErrores(), error = null) }
    }

    fun ingresar(onExito: () -> Unit) {
        if (_uiState.value.cargando) return
        if (_uiState.value.codigo.isBlank()) {
            _uiState.update { it.copy(errores = EstudianteErrores("Ingresa el código de tu actividad.")) }
            return
        }
        ejecutar(errorDeCodigo = true) {
            mostrar(buscarSesion(_uiState.value.codigo))
            onExito()
        }
    }

    private fun mostrar(sesion: SesionEstudiante) {
        _uiState.update { it.copy(sesion = sesion, seleccion = null, confirmar = false,
            preguntaRespondida = null, paso = if (sesion.finalizada) PasoEstudiante.RESULTADO else PasoEstudiante.INSTRUCCIONES) }
    }

    fun comenzar() { _uiState.update { it.copy(paso = PasoEstudiante.PREGUNTA) } }
    fun seleccionar(indice: Int) {
        val estado = _uiState.value
        if (estado.cargando || estado.paso != PasoEstudiante.PREGUNTA || estado.confirmar) return
        if (indice !in (estado.preguntaActual?.alternativas?.indices ?: return)) return
        _uiState.update { it.copy(seleccion = indice, error = null) }
    }
    fun solicitarConfirmacion() {
        if (_uiState.value.seleccion != null && !_uiState.value.cargando)
            _uiState.update { it.copy(confirmar = true) }
    }
    fun cancelarConfirmacion() {
        if (!_uiState.value.cargando) _uiState.update { it.copy(confirmar = false) }
    }
    fun confirmarRespuesta() {
        val estado = _uiState.value
        if (estado.cargando || !estado.confirmar) return
        val sesion = estado.sesion ?: return
        val pregunta = estado.preguntaActual ?: return
        val seleccion = estado.seleccion ?: return
        ejecutar {
            val actualizada = guardarRespuesta(sesion.intento.id, pregunta.id, seleccion)
            _uiState.update { it.copy(sesion = actualizada, confirmar = false,
                preguntaRespondida = pregunta, paso = PasoEstudiante.RETROALIMENTACION) }
        }
    }
    fun siguiente() {
        _uiState.update { it.copy(seleccion = null, preguntaRespondida = null,
            paso = if (it.sesion?.finalizada == true) PasoEstudiante.RESULTADO else PasoEstudiante.PREGUNTA) }
    }
    fun reintentar() {
        val id = _uiState.value.sesion?.intento?.id ?: return
        ejecutar { mostrar(crearReintento(id)) }
    }
    fun salir() {
        if (_uiState.value.cargando) return
        _uiState.value = EstudianteUiState()
    }
    private fun ejecutar(errorDeCodigo: Boolean = false, accion: suspend () -> Unit) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(cargando = true, error = null, errores = EstudianteErrores()) }
        viewModelScope.launch {
            try { accion() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                val mensaje = if (e is IllegalArgumentException) e.message ?: "Revisa los datos." else "No pudimos guardar o cargar los datos. Intenta nuevamente."
                _uiState.update { if (errorDeCodigo) it.copy(errores = EstudianteErrores(mensaje)) else it.copy(error = mensaje, confirmar = false) }
            } finally { _uiState.update { it.copy(cargando = false) } }
        }
    }

    // Operaciones de Room. Las transacciones evitan respuestas o intentos parciales.
    // Reingresar el código recupera el último intento; no gasta otro.
    internal suspend fun buscarSesion(codigo: String): SesionEstudiante = db.withTransaction {
        val normalizado = codigo.trim().uppercase(Locale.ROOT)
        require(normalizado.isNotBlank()) { "Ingresa el código de tu actividad." }
        // El código es temporal y pertenece a una asignación hecha por la asistente.
        val asignacion = requireNotNull(db.asignacionDao().porCodigo(normalizado)) { "No encontramos ese código. Revísalo con tu asistente." }
        val actividad = requireNotNull(db.actividadDao().porId(asignacion.actividadId)) { "La actividad asignada ya no existe." }
        validarActividad(actividad)
        val ultimo = dao.ultimoIntentoDeAsignacion(asignacion.id)
        val id = ultimo?.id ?: dao.insertarIntento(IntentoEntity(
            actividadId = actividad.id, asignacionId = asignacion.id, numero = 1,
            iniciadoEn = System.currentTimeMillis()))
        cargar(id)
    }

    private suspend fun validarActividad(actividad: ActividadEntity) {
        require(actividad.estado == "Disponible") { "Esta actividad todavía no está disponible. Consulta a tu asistente." }
        require(dao.preguntas(actividad.id).isNotEmpty()) { "Esta actividad aún no tiene preguntas preparadas." }
        require(actividad.intentosPermitidos > 0) { "Esta actividad no tiene intentos disponibles." }
    }

    private suspend fun cargar(id: Long): SesionEstudiante {
        val intento = requireNotNull(dao.intento(id)) { "No encontramos la sesión. Ingresa tu código nuevamente." }
        val actividad = requireNotNull(db.actividadDao().porId(intento.actividadId))
        return SesionEstudiante(actividad, dao.preguntas(actividad.id), intento, dao.respuestas(id))
    }

    internal suspend fun guardarRespuesta(intentoId: Long, preguntaId: Long, alternativa: Int): SesionEstudiante = db.withTransaction {
        val sesion = cargar(intentoId)
        validarActividad(sesion.actividad)
        require(!sesion.finalizada) { "El intento ya terminó." }
        val siguiente = sesion.preguntas.firstOrNull { p -> sesion.respuestas.none { it.preguntaId == p.id } }
        require(siguiente != null && siguiente.id == preguntaId) { "Esta pregunta ya fue respondida o no corresponde al paso actual." }
        require(alternativa in siguiente.alternativas.indices) { "Selecciona una alternativa válida." }
        dao.insertarRespuesta(RespuestaEntity(intentoId, preguntaId, alternativa,
            alternativa == siguiente.correcta, System.currentTimeMillis()))
        if (sesion.respuestas.size + 1 == sesion.preguntas.size) dao.finalizar(intentoId, System.currentTimeMillis())
        cargar(intentoId)
    }

    internal suspend fun crearReintento(intentoId: Long): SesionEstudiante = db.withTransaction {
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
        val id = dao.insertarIntento(IntentoEntity(actividadId = anterior.actividad.id,
            asignacionId = asignacionId,
            numero = ultimo.numero + 1, iniciadoEn = System.currentTimeMillis()))
        cargar(id)
    }
}
